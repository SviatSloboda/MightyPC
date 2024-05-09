package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.model.hardware.HardwareSpec;
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
class SsdControllerTest extends BaseControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private SsdRepository ssdRepository;

    private final SSD testSsd = new SSD("testSsd1",new HardwareSpec("testSsd1", "testDescription", BigDecimal.valueOf(350), 4.5f), 100,95);
    private final SSD testSsd2 = new SSD("testSsd2", new HardwareSpec("testSsd2", "testDescription", BigDecimal.valueOf(200), 4.0f), 1200,65);

    @BeforeEach
    void setUp(){
        ssdRepository.save(testSsd);
        ssdRepository.save(testSsd2);
    }

    @DirtiesContext
    @Test
    void saveSsd_shouldReturnCreatedSsd() throws Exception {
        String jsonRequestBody = """
                {
                    "hardwareSpec": {
                        "name": "testSsd1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/ssd")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testSsd1"));
    }

    @DirtiesContext
    @Test
    void saveAllSsds_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "hardwareSpec": {
                        "name": "testSsd2",
                        "description": "testDescription",
                        "price": 200,
                        "rating": 4.0
                    },
                    "energyConsumption": 65
                },
                {
                    "hardwareSpec": {
                        "name": "testSsd1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/ssd/all")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DirtiesContext
    @Test
    void getSortedSsdsByPrice_shouldReturnSortedData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/sort/price?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testSsd2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.name").value("testSsd1"));
    }

    @DirtiesContext
    @Test
    void getSortedSsdsByPrice_shouldReturnSortedDataInDescOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/sort/price?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testSsd1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.name").value("testSsd2"));
    }

    @DirtiesContext
    @Test
    void getSortedSsdsByPrice_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/sort/price?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredSsdsByPrice_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/filter/price?lowest=250&highest=400"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testSsd1"));
    }

    @DirtiesContext
    @Test
    void getSortedSsdsByRating_shouldReturnSortedDataAsc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/sort/rating?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.rating").value(4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.rating").value(4.5));
    }

    @DirtiesContext
    @Test
    void getFilteredSsdsByEnergyConsumption_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/filter/energy-consumption?lowest=70&highest=100"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].energyConsumption").value(95));
    }

    @DirtiesContext
    @Test
    void getFilteredSsdsByCapacity_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/filter/capacity?lowest=0&highest=120"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].energyConsumption").value(95));
    }

    @DirtiesContext
    @Test
    void getFilteredSsdsByEnergyConsumption_shouldReturnNotFoundWhenNoMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/filter/energy-consumption?lowest=100&highest=150"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @DirtiesContext
    @Test
    void getSortedSsdsByRating_shouldReturnNotFoundWhenNoSsdsExist() throws Exception {
        ssdRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/sort/rating?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void getSortedSsdsByRating_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/sort/rating?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredSsdsByPrice_shouldReturnNotFoundWhenNoMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/filter/price?lowest=500&highest=1000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @Override
    String getPathOfEntity() {
        return "ssd";
    }

    @Override
    String getIdOfEntity() {
        return "testSsd1";
    }

    @Override
    String getJsonRequestBodyForUpdate() {
        return """
                {
                    "id": "%s",
                    "hardwareSpec": {
                        "name": "Updated SSD",
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
        return "Updated SSD";
    }
}
