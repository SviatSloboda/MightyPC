package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.CpuRepository;
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
class CpuControllerTest extends BaseControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private CpuRepository cpuRepository;

    protected final CPU testCpu = new CPU("testCpu1", new HardwareSpec("testCpu1", "testDescription", BigDecimal.valueOf(350), 4.5f), 95, "LGA1151");
    protected final CPU testCpu2 = new CPU("testCpu2", new HardwareSpec("testCpu2", "testDescription", BigDecimal.valueOf(200), 4.0f), 65, "AM4");

    @BeforeEach
    void setUp(){
        cpuRepository.save(testCpu);
        cpuRepository.save(testCpu2);
    }

    @DirtiesContext
    @Test
    void saveCpu_shouldReturnCreatedCpu() throws Exception {
        String jsonRequestBody = """
                {
                    "hardwareSpec": {
                        "name": "testCpu1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95,
                    "socket": "LGA1151"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/cpu")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testCpu1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.socket").value("LGA1151"));
    }

    @DirtiesContext
    @Test
    void saveAllCpus_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "hardwareSpec": {
                        "name": "testCpu2",
                        "description": "testDescription",
                        "price": 200,
                        "rating": 4.0
                    },
                    "energyConsumption": 65,
                    "socket": "AM4"
                },
                {
                    "hardwareSpec": {
                        "name": "testCpu1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95,
                    "socket": "LGA1151"
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/cpu/all")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DirtiesContext
    @Test
    void getSortedCpusByPriceAsc_shouldReturnSortedData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/sort/price?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value("testCpu2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value("testCpu1"));
    }

    @DirtiesContext
    @Test
    void getSortedCpusByPrice_shouldReturnSortedDataInDescOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/sort/price?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testCpu1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.name").value("testCpu2"));
    }

    @DirtiesContext
    @Test
    void getSortedCpusByPrice_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/sort/price?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredCpusByPrice_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/filter/price?lowest=250&highest=400"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testCpu1"));
    }

    @DirtiesContext
    @Test
    void getSortedCpusByRatingAsc_shouldReturnSortedData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/sort/rating?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value("testCpu2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id").value("testCpu1"));
    }

    @DirtiesContext
    @Test
    void getFilteredCpusBySocket_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/filter/socket?socket=AM4"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].socket").value("AM4"));
    }

    @DirtiesContext
    @Test
    void getFilteredCpusByEnergyConsumption_shouldReturnFilteredData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/filter/energy-consumption?lowest=70&highest=100"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].energyConsumption").value(95));
    }

    @DirtiesContext
    @Test
    void getSocketOfCpu_shouldReturnSocket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/socket/" + testCpu.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("LGA1151"));
    }

    @DirtiesContext
    @Test
    void getFilteredCpusBySocket_shouldReturnNotFoundWhenNoMatch() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/filter/socket?socket=AM5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @DirtiesContext
    @Test
    void getFilteredCpusByEnergyConsumption_shouldReturnNotFoundWhenNoMatch() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/filter/energy-consumption?lowest=100&highest=150"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @DirtiesContext
    @Test
    void getSortedCpusByRating_shouldReturnNotFoundWhenNoCpusExist() throws Exception {
        cpuRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/sort/rating?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void getSortedCpusByRating_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/sort/rating?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredCpusByPrice_shouldReturnNotFoundWhenNoMatch() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/filter/price?lowest=500&highest=1000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @Override
    String getPathOfEntity() {
        return "cpu";
    }

    @Override
    String getIdOfEntity() {
        return "testCpu1";
    }

    @Override
    String getJsonRequestBodyForUpdate() {
        return """
                {
                    "id": "%s",
                    "hardwareSpec": {
                        "name": "Updated CPU",
                        "description": "Updated description",
                        "price": 250,
                        "rating": 4.6
                    },
                    "energyConsumption": 85,
                    "socket": "UpdatedSocket"
                }
                """;
    }

    @Override
    String getUpdatedNameOfEntity() {
        return "Updated CPU";
    }
}
