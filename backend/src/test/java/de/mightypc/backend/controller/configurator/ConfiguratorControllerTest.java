package de.mightypc.backend.controller.configurator;

import de.mightypc.backend.model.hardware.*;
import de.mightypc.backend.repository.hardware.*;
import de.mightypc.backend.security.SecurityConfig;
import de.mightypc.backend.service.configurator.ChatGptService;
import de.mightypc.backend.service.configurator.ConfiguratorService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser
class ConfiguratorControllerTest {

    private final CPU cpu = new CPU("cpuId", new HardwareSpec("testCpu", "test", new BigDecimal(50), 2.5f), 10, "AM4");
    private final GPU gpu = new GPU("gpuId", new HardwareSpec("testGpu", "test", new BigDecimal(50), 2.5f), 10);
    private final Motherboard motherboard = new Motherboard("motherboardId", new HardwareSpec("testMotherboard", "test", new BigDecimal(50), 2.5f), 10, "AM4");
    private final RAM ram = new RAM("ramId", new HardwareSpec("testRam", "test", new BigDecimal(50), 2.5f), "DDR", 10, 2);
    private final SSD ssd = new SSD("ssdId", new HardwareSpec("testSsd", "test", new BigDecimal(50), 2.5f), 5, 10);
    private final HDD hdd = new HDD("hddId", new HardwareSpec("testHdd", "test", new BigDecimal(50), 2.5f), 5, 10);
    private final PcCase pcCase = new PcCase("pcCaseId", new HardwareSpec("testPcCase", "test", new BigDecimal(50), 2.5f), "3x3x3");
    private final PowerSupply powerSupply = new PowerSupply("powerSupplyId", new HardwareSpec("testPowerSupply", "test", new BigDecimal(50), 2.5f), 400);

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private CpuRepository cpuRepository;
    @Autowired
    private GpuRepository gpuRepository;
    @Autowired
    private MotherboardRepository motherboardRepository;
    @Autowired
    private RamRepository ramRepository;
    @Autowired
    private SsdRepository ssdRepository;
    @Autowired
    private HddRepository hddRepository;
    @Autowired
    private PcCaseRepository pcCaseRepository;
    @Autowired
    private PowerSupplyRepository powerSupplyRepository;

    @Autowired
    private ConfiguratorService configuratorService;
    @Autowired
    private ChatGptService chatGptService;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        chatGptService.setRestClient(mockWebServer.url("/").toString(), "fake-api-key", "fake-org");

        cpuRepository.save(cpu);
        gpuRepository.save(gpu);
        motherboardRepository.save(motherboard);
        ramRepository.save(ram);
        ssdRepository.save(ssd);
        hddRepository.save(hdd);
        pcCaseRepository.save(pcCase);
        powerSupplyRepository.save(powerSupply);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DirtiesContext
    @Test
    void getAllConfiguratorComponents_shouldReturnComponents() throws Exception {
        mockMvc.perform(get("/api/configurator/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemsForConfigurator").isArray())
                .andExpect(jsonPath("$.itemsForConfigurator.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$.itemsForConfigurator[0]").isArray());
    }

    @DirtiesContext
    @Test
    void getAllMotherboardIdsAndNamesWithPrices_shouldReturnMotherboards() throws Exception {
        mockMvc.perform(get("/api/configurator/motherboard/socket/{cpuSocket}", "AM4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @DirtiesContext
    @Test
    void getAllPowerSuppliesByEnergyConsumption_shouldReturnPowerSupplies() throws Exception {
        mockMvc.perform(post("/api/configurator/power-supply/energyConsumption/{energyConsumption}", 500))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @DirtiesContext
    @Test
    void createPcWithChatGpt_shouldReturnSpecsIds() throws Exception {
        String mockResponseBody = """
                {
                    "choices": [
                        {
                            "message": {
                                "role": "assistant",
                                "content": "cpuId,gpuId,motherboardId,ramId,ssdId,hddId,powerSupplyId,pcCaseId"
                            }
                        }
                    ]
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseBody)
                .addHeader("Content-Type", "application/json"));

        String requestBody = """
                ["gaming", "1500"]
                """;

        mockMvc.perform(post("/api/configurator/gpt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpuId").value("cpuId"))
                .andExpect(jsonPath("$.gpuId").value("gpuId"))
                .andExpect(jsonPath("$.motherboardId").value("motherboardId"))
                .andExpect(jsonPath("$.ramId").value("ramId"))
                .andExpect(jsonPath("$.ssdId").value("ssdId"))
                .andExpect(jsonPath("$.hddId").value("hddId"))
                .andExpect(jsonPath("$.powerSupplyId").value("powerSupplyId"))
                .andExpect(jsonPath("$.pcCaseId").value("pcCaseId"));
    }
}
