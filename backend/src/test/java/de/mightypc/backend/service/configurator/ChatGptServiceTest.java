package de.mightypc.backend.service.configurator;

import de.mightypc.backend.model.hardware.*;
import de.mightypc.backend.repository.hardware.*;
import de.mightypc.backend.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser
class ChatGptServiceTest {

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
    private MockMvc mockMvc;

    @MockBean
    private ChatGptService chatGptService;

    private final CPU cpu = new CPU("cpuId", new HardwareSpec("testCpu", "test", new BigDecimal(10), 2.5f), 5, "AM4");
    private final GPU gpu = new GPU("gpuId", new HardwareSpec("testGpu", "test", new BigDecimal(10), 2.5f), 5);
    private final Motherboard motherboard = new Motherboard("motherboardId", new HardwareSpec("testMotherboard", "test", new BigDecimal(10), 2.5f), 5, "AM4");
    private final RAM ram = new RAM("ramId", new HardwareSpec("testRam", "test", new BigDecimal(10), 2.5f), "DDR", 5, 2);
    private final SSD ssd = new SSD("ssdId", new HardwareSpec("testSsd", "test", new BigDecimal(10), 2.5f), 5, 5);
    private final HDD hdd = new HDD("hddId", new HardwareSpec("testHdd", "test", new BigDecimal(10), 2.5f), 5, 5);
    private final PcCase pcCase = new PcCase("pcCaseId", new HardwareSpec("testPcCase", "test", new BigDecimal(10), 2.5f), "3x3x3");
    private final PowerSupply powerSupply = new PowerSupply("powerSupplyId", new HardwareSpec("testPowerSupply", "test", new BigDecimal(10), 2.5f), 5);

    @BeforeEach
    void setUp() {
        cpuRepository.save(cpu);
        gpuRepository.save(gpu);
        motherboardRepository.save(motherboard);
        ramRepository.save(ram);
        ssdRepository.save(ssd);
        hddRepository.save(hdd);
        powerSupplyRepository.save(powerSupply);
        pcCaseRepository.save(pcCase);

        when(chatGptService.createChatGptRecommendation("gaming", "1500"))
                .thenReturn(new SpecsIds("cpuId", "gpuId", "motherboardId", "ramId", "ssdId", "hddId", "powerSupplyId", "pcCaseId"));
    }

    @Test
    void createChatGptRecommendation_shouldReturnSpecsIds() throws Exception {
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