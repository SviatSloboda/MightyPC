package de.mightypc.backend.controller.configurator;

import de.mightypc.backend.model.hardware.*;
import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.repository.hardware.GpuRepository;
import de.mightypc.backend.repository.hardware.HddRepository;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import de.mightypc.backend.repository.hardware.RamRepository;
import de.mightypc.backend.repository.hardware.SsdRepository;
import de.mightypc.backend.repository.pc.PcRepository;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser
class UserPcsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PcRepository pcRepository;

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
    private UserRepository userRepository;

    private final CPU cpu = new CPU("cpuId", new HardwareSpec("testCpu", "test", new BigDecimal(50), 2.5f), 10, "AM4");
    private final GPU gpu = new GPU("gpuId", new HardwareSpec("testGpu", "test", new BigDecimal(50), 2.5f), 10);
    private final Motherboard motherboard = new Motherboard("motherboardId", new HardwareSpec("testMotherboard", "test", new BigDecimal(50), 2.5f), 10, "AM4");
    private final RAM ram = new RAM("ramId", new HardwareSpec("testRam", "test", new BigDecimal(50), 2.5f), "DDR", 10, 2);
    private final SSD ssd = new SSD("ssdId", new HardwareSpec("testSsd", "test", new BigDecimal(50), 2.5f), 5, 10);
    private final HDD hdd = new HDD("hddId", new HardwareSpec("testHdd", "test", new BigDecimal(50), 2.5f), 5, 10);
    private final PcCase pcCase = new PcCase("pcCaseId", new HardwareSpec("testPcCase", "test", new BigDecimal(50), 2.5f), "3x3x3");
    private final PowerSupply powerSupply = new PowerSupply("powerSupplyId", new HardwareSpec("testPowerSupply", "test", new BigDecimal(50), 2.5f), 600);

    private final CPU cpu2 = new CPU("cpuId2", new HardwareSpec("testCpu2", "test", new BigDecimal(10), 2.5f), 5, "AM4");
    private final GPU gpu2 = new GPU("gpuId2", new HardwareSpec("testGpu2", "test", new BigDecimal(10), 2.5f), 5);
    private final Motherboard motherboard2 = new Motherboard("motherboardId2", new HardwareSpec("testMotherboard2", "test", new BigDecimal(10), 2.5f), 5, "AM4");
    private final RAM ram2 = new RAM("ramId2", new HardwareSpec("testRam2", "test", new BigDecimal(10), 2.5f), "DDR", 5, 2);
    private final SSD ssd2 = new SSD("ssdId2", new HardwareSpec("testSsd2", "test", new BigDecimal(10), 2.5f), 5, 5);
    private final HDD hdd2 = new HDD("hddId2", new HardwareSpec("testHdd2", "test", new BigDecimal(10), 2.5f), 5, 5);
    private final PcCase pcCase2 = new PcCase("pcCaseId2", new HardwareSpec("testPcCase2", "test", new BigDecimal(10), 2.5f), "3x3x3");
    private final PowerSupply powerSupply2 = new PowerSupply("powerSupplyId2", new HardwareSpec("testPowerSupply2", "test", new BigDecimal(10), 2.5f), 500);

    private final Specs specs = new Specs(cpu, gpu, motherboard, ram, ssd, hdd, powerSupply, pcCase);
    private final Specs specs2 = new Specs(cpu2, gpu2, motherboard2, ram2, ssd2, hdd2, powerSupply2, pcCase2);

    private PC testPc = new PC(
            "pcId1",
            new HardwareSpec("testPc1", "testDescription", new BigDecimal(350), 4.5f),
            specs,
            95,
            Collections.emptyList()
    );

    private final PC testPc2 = new PC(
            "pcId2",
            new HardwareSpec("testPc2", "testDescription", new BigDecimal(200), 4.00f),
            specs2,
            65,
            Collections.emptyList()
    );

    private final User user = new User(
            "user1",
            "testEmail",
            "testPassword",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(List.of(testPc, testPc2)),
            true,
            "user",
            "23.32",
            "link"
    );


    @BeforeEach
    void setup(){
        cpuRepository.save(cpu);
        gpuRepository.save(gpu);
        motherboardRepository.save(motherboard);
        ramRepository.save(ram);
        ssdRepository.save(ssd);
        hddRepository.save(hdd);
        pcCaseRepository.save(pcCase);
        powerSupplyRepository.save(powerSupply);

        cpuRepository.save(cpu2);
        gpuRepository.save(gpu2);
        motherboardRepository.save(motherboard2);
        ramRepository.save(ram2);
        ssdRepository.save(ssd2);
        hddRepository.save(hdd2);
        pcCaseRepository.save(pcCase2);
        powerSupplyRepository.save(powerSupply2);

        userRepository.save(user);
    }

    @DirtiesContext
    @Test
    void getAllByPage_shouldReturnPagedPcResponses() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user-pcs/{userId}/page", "user1")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value("pcId1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value("pcId2"));
    }

    @DirtiesContext
    @Test
    void getPcById_shouldReturnPcDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user-pcs/{userId}/{pcId}", "user1", "pcId1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("pcId1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testPc1"));
    }

    @DirtiesContext
    @Test
    void deletePcById_shouldDeletePc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user-pcs/{userId}/{pcId}", "user1", "pcId1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DirtiesContext
    @Test
    void update_shouldUpdatePcAndReturnOk() throws Exception {
        String requestBody = """
            {
                "id": "pcId1",
                "hardwareSpec": {
                    "name": "Updated Gaming PC",
                    "description": "Updated description",
                    "price": 1500,
                    "rating": 4.9
                },
                "specsIds": {
                    "cpuId": "cpuId",
                    "gpuId": "gpuId",
                    "motherboardId": "motherboardId",
                    "ramId": "ramId",
                    "ssdId": "ssdId",
                    "hddId": "hddId",
                    "powerSupplyId": "powerSupplyId",
                    "pcCaseId": "pcCaseId"
                }
            }
            """;
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user-pcs/{userId}", "user1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DirtiesContext
    @Test
    void addPrivateUserPcToAllPcs_shouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user-pcs/{userId}/{pcId}/promote", "user1", "pcId1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DirtiesContext
    @Test
    void createUserPc_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
            {
                "specsIds": {
                    "cpuId": "cpuId",
                    "gpuId": "gpuId",
                    "motherboardId": "motherboardId",
                    "ramId": "ramId",
                    "ssdId": "ssdId",
                    "hddId": "hddId",
                    "powerSupplyId": "powerSupplyId",
                    "pcCaseId": "pcCaseId"
                },
                "hardwareSpec": {
                    "name": "New Custom PC",
                    "description": "Custom built PC",
                    "price": 2000,
                    "rating": 5.0
                }
            }
            """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user-pcs/{userId}", "user1")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DirtiesContext
    @Test
    void clearBasket_shouldClearAllPcs() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user-pcs/{userId}/all", "user1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
