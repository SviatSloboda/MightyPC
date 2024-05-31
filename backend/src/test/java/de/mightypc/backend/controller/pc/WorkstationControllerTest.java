package de.mightypc.backend.controller.pc;

import de.mightypc.backend.model.hardware.*;
import de.mightypc.backend.model.pc.Workstation;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.repository.hardware.GpuRepository;
import de.mightypc.backend.repository.hardware.HddRepository;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import de.mightypc.backend.repository.hardware.RamRepository;
import de.mightypc.backend.repository.hardware.SsdRepository;
import de.mightypc.backend.repository.pc.WorkstationRepository;
import de.mightypc.backend.security.SecurityConfig;
import org.hamcrest.Matchers;
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
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class WorkstationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WorkstationRepository workstationRepository;

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

    private final CPU cpu = new CPU("cpuId", new HardwareSpec("testCpu", "test", new BigDecimal(50), 2.5f), 10, "AM4");
    private final GPU gpu = new GPU("gpuId", new HardwareSpec("testGpu", "test", new BigDecimal(50), 2.5f), 10);
    private final Motherboard motherboard = new Motherboard("motherboardId", new HardwareSpec("testMotherboard", "test", new BigDecimal(50), 2.5f), 10, "AM4");
    private final RAM ram = new RAM("ramId", new HardwareSpec("testRam", "test", new BigDecimal(50), 2.5f), "DDR", 10, 2);
    private final SSD ssd = new SSD("ssdId", new HardwareSpec("testSsd", "test", new BigDecimal(50), 2.5f), 5, 10);
    private final HDD hdd = new HDD("hddId", new HardwareSpec("testHdd", "test", new BigDecimal(50), 2.5f), 5, 10);
    private final PcCase pcCase = new PcCase("pcCaseId", new HardwareSpec("testWorkstationCase", "test", new BigDecimal(50), 2.5f), "3x3x3");
    private final PowerSupply powerSupply = new PowerSupply("powerSupplyId", new HardwareSpec("testPowerSupply", "test", new BigDecimal(50), 2.5f), 600);

    private final CPU cpu2 = new CPU("cpuId2", new HardwareSpec("testCpu2", "test", new BigDecimal(10), 2.5f), 5, "AM4");
    private final GPU gpu2 = new GPU("gpuId2", new HardwareSpec("testGpu2", "test", new BigDecimal(10), 2.5f), 5);
    private final Motherboard motherboard2 = new Motherboard("motherboardId2", new HardwareSpec("testMotherboard2", "test", new BigDecimal(10), 2.5f), 5, "AM4");
    private final RAM ram2 = new RAM("ramId2", new HardwareSpec("testRam2", "test", new BigDecimal(10), 2.5f), "DDR", 5, 2);
    private final SSD ssd2 = new SSD("ssdId2", new HardwareSpec("testSsd2", "test", new BigDecimal(10), 2.5f), 5, 5);
    private final HDD hdd2 = new HDD("hddId2", new HardwareSpec("testHdd2", "test", new BigDecimal(10), 2.5f), 5, 5);
    private final PcCase pcCase2 = new PcCase("pcCaseId2", new HardwareSpec("testWorkstationCase2", "test", new BigDecimal(10), 2.5f), "3x3x3");
    private final PowerSupply powerSupply2 = new PowerSupply("powerSupplyId2", new HardwareSpec("testPowerSupply2", "test", new BigDecimal(10), 2.5f), 500);

    private final Specs specs = new Specs(cpu, gpu, motherboard, ram, ssd, hdd, powerSupply, pcCase);
    private final Specs specs2 = new Specs(cpu2, gpu2, motherboard2, ram2, ssd2, hdd2, powerSupply2, pcCase2);

    private Workstation testWorkstation = new Workstation(
            "testWorkstationId1",
            new HardwareSpec("testWorkstation1", "testDescription", new BigDecimal(350), 4.5f),
            specs,
            95,
            2,
            2,
            Collections.emptyList()
    );

    private final Workstation testWorkstation2 = new Workstation(
            "testWorkstationId2",
            new HardwareSpec("testWorkstation2", "testDescription", new BigDecimal(200), 4.00f),
            specs2,
            65,
            1,
            1,
            Collections.emptyList()
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
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void save_shouldCreateWorkstationAndReturnStatusCreated() throws Exception {
        String requestBody = """
            {
                "hardwareSpec": {
                    "name": "Gaming Workstation",
                    "description": "High-end gaming Workstation",
                    "price": 1500,
                    "rating": 4.7
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
                },
                "cpuNumber": "1",
                "gpuNumber": "1"
            }
            """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/workstation")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void saveAll_shouldCreateMultipleWorkstationsAndReturnStatusCreated() throws Exception {
        String requestBody = """
            [{
                "hardwareSpec": {
                    "name": "Gaming Workstation",
                    "description": "High-end gaming Workstation",
                    "price": 1500,
                    "rating": 4.7
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
                },
                "cpuNumber" : 1,
                "gpuNumber" : 1
            },
            {
                "hardwareSpec": {
                    "name": "Office Workstation",
                    "description": "Efficient office Workstation",
                    "price": 500,
                    "rating": 4.0
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
                },
                "cpuNumber" : 1,
                "gpuNumber" : 1
            }]
            """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/workstation/all")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DirtiesContext
    @Test
    void getWorkstations_shouldReturnWorkstationsFilteredByPriceRange() throws Exception {
        workstationRepository.save(testWorkstation);
        workstationRepository.save(testWorkstation2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/workstation/filtered")
                        .param("page", "0")
                        .param("size", "8")
                        .param("lowestPrice", "100")
                        .param("highestPrice", "300"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.price").value(200));
    }

    @DirtiesContext
    @Test
    void getWorkstations_shouldReturnWorkstationsSortedByRatingDesc() throws Exception {
        workstationRepository.save(testWorkstation);
        workstationRepository.save(testWorkstation2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/workstation/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "rating-desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.rating").value(4.5f));
    }

    @DirtiesContext
    @Test
    void getWorkstations_shouldReturnAllWorkstations_whenNoFiltersApplied() throws Exception {
        workstationRepository.save(testWorkstation);
        workstationRepository.save(testWorkstation2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/workstation/filtered")
                        .param("page", "0")
                        .param("size", "8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)));
    }

    @DirtiesContext
    @Test
    void getWorkstations_shouldReturnWorkstationsSortedByPriceAsc() throws Exception {
        workstationRepository.save(testWorkstation);
        workstationRepository.save(testWorkstation2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/workstation/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "price-asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.price").value(200));
    }

    @DirtiesContext
    @Test
    void getWorkstations_shouldReturnWorkstationsSortedByPriceDesc() throws Exception {
        workstationRepository.save(testWorkstation);
        workstationRepository.save(testWorkstation2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/workstation/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "price-desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.price").value(350));
    }

    @DirtiesContext
    @Test
    void getWorkstations_shouldReturnWorkstationsSortedByRatingAsc() throws Exception {
        workstationRepository.save(testWorkstation);
        workstationRepository.save(testWorkstation2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/workstation/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "rating-asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.rating").value(4.0f));
    }

    @DirtiesContext
    @Test
    void getAllByPage_shouldReturnPagedWorkstationResponses() throws Exception {
        workstationRepository.save(testWorkstation);
        workstationRepository.save(testWorkstation2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/workstation/page")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(testWorkstation.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value(testWorkstation2.id()));
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void deleteById_shouldDeleteWorkstationWithSpecifiedId() throws Exception {
        workstationRepository.save(testWorkstation);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/workstation/{id}", testWorkstation.id()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(workstationRepository.findAll().isEmpty());
    }


    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void updateWorkstationResponse_shouldUpdateWorkstationAndReturnOk() throws Exception {
        workstationRepository.save(testWorkstation);
        String updatedName = "Updated Gaming Workstation";

        testWorkstation = testWorkstation.withHardwareSpec(new HardwareSpec("Updated Gaming Workstation", "testDescription", new BigDecimal(350), 4.5f));

        String requestBody = """
        {
            "id": "%s",
            "hardwareSpec": {
                "name": "%s",
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
            },
            "cpuNumber" : 1,
            "gpuNumber" : 1
        }
        """.formatted(testWorkstation.id(), updatedName);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/workstation")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/workstation/" + testWorkstation.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value(updatedName));
    }

}