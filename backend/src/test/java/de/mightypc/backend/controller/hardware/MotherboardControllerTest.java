package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
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
class MotherboardControllerTest extends BaseControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private MotherboardRepository motherboardRepository;

    private final Motherboard testMotherboard = new Motherboard("testMotherboard1",new HardwareSpec("testMotherboard1", "testDescription", BigDecimal.valueOf(350), 4.5f), 95, "LGA1151");
    private final Motherboard testMotherboard2 = new Motherboard("testMotherboard2",new HardwareSpec("testMotherboard2", "testDescription", BigDecimal.valueOf(200), 4.0f), 65, "AM4");

    @BeforeEach
    void setUp(){
        motherboardRepository.save(testMotherboard);
        motherboardRepository.save(testMotherboard2);
    }

    @DirtiesContext
    @Test
    void saveMotherboard_shouldReturnCreatedMotherboard() throws Exception {
        String jsonRequestBody = """
                {
                    "hardwareSpec": {
                        "name": "testMotherboard1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95,
                    "socket": "LGA1151"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/motherboard")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testMotherboard1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.socket").value("LGA1151"));
    }

    @DirtiesContext
    @Test
    void saveAllMotherboards_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "hardwareSpec": {
                        "name": "testMotherboard2",
                        "description": "testDescription",
                        "price": 200,
                        "rating": 4.0
                    },
                    "energyConsumption": 65,
                    "socket": "AM4"
                },
                {
                    "hardwareSpec": {
                        "name": "testMotherboard1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95,
                    "socket": "LGA1151"
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/motherboard/all")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DirtiesContext
    @Test
    void getSortedMotherboardsByPrice_shouldReturnSortedData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/sort/price?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testMotherboard2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.name").value("testMotherboard1"));
    }

    @DirtiesContext
    @Test
    void getSortedMotherboardsByPrice_shouldReturnSortedDataInDescOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/sort/price?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testMotherboard1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.name").value("testMotherboard2"));
    }

    @DirtiesContext
    @Test
    void getSortedMotherboardsByPrice_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/sort/price?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredMotherboardsByPrice_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/filter/price?lowest=250&highest=400"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testMotherboard1"));
    }

    @DirtiesContext
    @Test
    void getSortedMotherboardsByRating_shouldReturnSortedData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/sort/rating?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.rating").value(4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.rating").value(4.5));
    }

    @DirtiesContext
    @Test
    void getFilteredMotherboardsBySocket_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/filter/socket?socket=AM4"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].socket").value("AM4"));
    }

    @DirtiesContext
    @Test
    void getFilteredMotherboardsByEnergyConsumption_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/filter/energy-consumption?lowest=70&highest=100"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].energyConsumption").value(95));
    }

    @DirtiesContext
    @Test
    void getFilteredMotherboardsBySocket_shouldReturnNotFoundWhenNoMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/filter/socket?socket=AM5"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @DirtiesContext
    @Test
    void getFilteredMotherboardsByEnergyConsumption_shouldReturnNotFoundWhenNoMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/filter/energy-consumption?lowest=100&highest=150"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @DirtiesContext
    @Test
    void getSortedMotherboardsByRating_shouldReturnNotFoundWhenNoMotherboardsExist() throws Exception {
        motherboardRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/sort/rating?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void getSortedMotherboardsByRating_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/sort/rating?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredMotherboardsByPrice_shouldReturnNotFoundWhenNoMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/filter/price?lowest=500&highest=1000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @Override
    String getPathOfEntity() {
        return "motherboard";
    }

    @Override
    String getIdOfEntity() {
        return "testMotherboard1";
    }

    @Override
    String getJsonRequestBodyForUpdate() {
        return """
                {
                    "id": "%s",
                    "hardwareSpec": {
                        "name": "Updated Motherboard",
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
        return "Updated Motherboard";
    }
}
