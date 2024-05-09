package de.mightypc.backend.controller.configurator;

import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.model.hardware.PowerSupply;
import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.repository.hardware.GpuRepository;
import de.mightypc.backend.repository.hardware.HddRepository;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import de.mightypc.backend.repository.hardware.RamRepository;
import de.mightypc.backend.repository.hardware.SsdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
class ConfiguratorControllerTest {

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

    private final CPU cpu = new CPU("cpuId", new HardwareSpec("testCpu", "test", new BigDecimal(50), 2.5f), 10, "AM4");
    private final GPU gpu = new GPU("gpuId", new HardwareSpec("testGpu", "test", new BigDecimal(50), 2.5f), 10);
    private final Motherboard motherboard = new Motherboard("motherboardId", new HardwareSpec("testMotherboard", "test", new BigDecimal(50), 2.5f), 10, "AM4");
    private final RAM ram = new RAM("ramId", new HardwareSpec("testRam", "test", new BigDecimal(50), 2.5f), "DDR", 10, 2);
    private final SSD ssd = new SSD("ssdId", new HardwareSpec("testSsd", "test", new BigDecimal(50), 2.5f), 5, 10);
    private final HDD hdd = new HDD("hddId", new HardwareSpec("testHdd", "test", new BigDecimal(50), 2.5f), 5, 10);
    private final PcCase pcCase = new PcCase("pcCaseId", new HardwareSpec("testPcCase", "test", new BigDecimal(50), 2.5f), "3x3x3");
    private final PowerSupply powerSupply = new PowerSupply("powerSupplyId", new HardwareSpec("testPowerSupply", "test", new BigDecimal(50), 2.5f), 400);

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
    }

    @DirtiesContext
    @Test
    void getAllConfiguratorComponents_shouldReturnComponents() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/configurator"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.componentIdsAndNames").exists());
    }

    @DirtiesContext
    @Test
    void getAllMotherboardIdsAndNamesWithPrices_shouldReturnMotherboards() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/configurator/motherboard/socket/{cpuSocket}", "AM4"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isMap());
    }

    @DirtiesContext
    @Test
    void getAllPowerSuppliesByEnergyConsumption_shouldReturnPowerSupplies() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/configurator/power-supply/energyConsumption/{energyConsumption}", 500))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isMap());
    }
}
