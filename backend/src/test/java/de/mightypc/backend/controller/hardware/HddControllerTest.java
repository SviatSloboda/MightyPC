package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.HddRepository;
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
class HddControllerTest extends BaseControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private HddRepository hddRepository;

    private final HDD testHdd = new HDD("testHdd1", new HardwareSpec("testHdd1", "testDescription", BigDecimal.valueOf(350), 4.5f), 100, 95);
    private final HDD testHdd2 = new HDD("testHdd2", new HardwareSpec("testHdd2", "testDescription", BigDecimal.valueOf(200), 4.0f), 1200, 65);

    @BeforeEach
    void setUp(){
        hddRepository.save(testHdd);
        hddRepository.save(testHdd2);
    }

    @DirtiesContext
    @Test
    void saveHdd_shouldReturnCreatedHdd() throws Exception {
        String jsonRequestBody = """
                {
                    "hardwareSpec": {
                        "name": "testHdd1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/hdd")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testHdd1"));
    }

    @DirtiesContext
    @Test
    void saveAllHdds_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "hardwareSpec": {
                        "name": "testHdd2",
                        "description": "testDescription",
                        "price": 200,
                        "rating": 4.0
                    },
                    "energyConsumption": 65
                },
                {
                    "hardwareSpec": {
                        "name": "testHdd1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/hdd/all")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DirtiesContext
    @Test
    void getSortedHddsByPrice_shouldReturnSortedData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/sort/price?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testHdd2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.name").value("testHdd1"));
    }

    @DirtiesContext
    @Test
    void getSortedHddsByPrice_shouldReturnSortedDataInDescOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/sort/price?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testHdd1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.name").value("testHdd2"));
    }

    @DirtiesContext
    @Test
    void getSortedHddsByPrice_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/sort/price?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredHddsByPrice_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/filter/price?lowest=250&highest=400"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testHdd1"));
    }

    @DirtiesContext
    @Test
    void getSortedHddsByRating_shouldReturnSortedDataAsc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/sort/rating?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.rating").value(4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.rating").value(4.5));
    }

    @DirtiesContext
    @Test
    void getFilteredHddsByEnergyConsumption_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/filter/energy-consumption?lowest=70&highest=100"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].energyConsumption").value(95));
    }

    @DirtiesContext
    @Test
    void getFilteredHddsByCapacity_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/filter/capacity?lowest=0&highest=120"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].energyConsumption").value(95));
    }

    @DirtiesContext
    @Test
    void getFilteredHddsByEnergyConsumption_shouldReturnNotFoundWhenNoMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/filter/energy-consumption?lowest=100&highest=150"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @DirtiesContext
    @Test
    void getSortedHddsByRating_shouldReturnNotFoundWhenNoHddsExist() throws Exception {
        hddRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/sort/rating?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void getSortedHddsByRating_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/sort/rating?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredHddsByPrice_shouldReturnNotFoundWhenNoMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/filter/price?lowest=500&highest=1000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @Override
    String getPathOfEntity() {
        return "hdd";
    }

    @Override
    String getIdOfEntity() {
        return "testHdd1";
    }

    @Override
    String getJsonRequestBodyForUpdate() {
        return """
                {
                    "id": "%s",
                    "hardwareSpec": {
                        "name": "Updated HDD",
                        "description": "Updated description",
                        "price": 250,
                        "rating": 4.6
                    },
                    "energyConsumption": 85,
                    "capacity": 23
                }
                """;
    }

    @Override
    String getUpdatedNameOfEntity() {
        return "Updated HDD";
    }
}
