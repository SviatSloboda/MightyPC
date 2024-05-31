package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.SsdRepository;
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
class SsdControllerTest extends BaseControllerTest {
    private final SSD testSsd = new SSD("testSsd1", new HardwareSpec("testSsd1", "testDescription", BigDecimal.valueOf(350), 4.5f), 100, 95);
    private final SSD testSsd2 = new SSD("testSsd2", new HardwareSpec("testSsd2", "testDescription", BigDecimal.valueOf(200), 4.0f), 1200, 65);
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private SsdRepository ssdRepository;

    @BeforeEach
    void setUp() {
        ssdRepository.save(testSsd);
        ssdRepository.save(testSsd2);
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(roles = "ADMIN")
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
