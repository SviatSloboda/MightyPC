package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
class MotherboardControllerTest extends BaseControllerTest {
    private final Motherboard testMotherboard = new Motherboard("testMotherboard1", new HardwareSpec("testMotherboard1", "testDescription", BigDecimal.valueOf(350), 4.5f), 95, "LGA1151");
    private final Motherboard testMotherboard2 = new Motherboard("testMotherboard2", new HardwareSpec("testMotherboard2", "testDescription", BigDecimal.valueOf(200), 4.0f), 65, "AM4");
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private MotherboardRepository motherboardRepository;

    @BeforeEach
    void setUp() {
        motherboardRepository.save(testMotherboard);
        motherboardRepository.save(testMotherboard2);
    }

    @DirtiesContext
    @WithMockUser
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
    @WithMockUser
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
