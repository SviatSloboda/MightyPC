package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.RamRepository;
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
class RamControllerTest extends BaseControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private RamRepository ramRepository;

    private final RAM testRam = new RAM("testRam1",new HardwareSpec("testRam1", "testDescription", BigDecimal.valueOf(350), 4.5f), "ddr4", 95, 100);
    private final RAM testRam2 = new RAM("testRam2",new HardwareSpec("testRam2", "testDescription", BigDecimal.valueOf(200), 4.0f), "ddr5", 65, 1200);

    @BeforeEach
    void setUp(){
        ramRepository.save(testRam);
        ramRepository.save(testRam2);
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void saveRam_shouldReturnCreatedRam() throws Exception {
        String jsonRequestBody = """
                {
                    "hardwareSpec": {
                        "name": "testRam1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "type" : "ddr4",
                    "energyConsumption": 95,
                    "memorySize" : 32
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/ram")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testRam1"));
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void saveAllRams_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "hardwareSpec": {
                        "name": "testRam2",
                        "description": "testDescription",
                        "price": 200,
                        "rating": 4.0
                    },
                    "type": "ddr4",
                    "energyConsumption": 65,
                    "memorySize" : 32
                },
                {
                    "hardwareSpec": {
                        "name": "testRam1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "type" : "ddr4",
                    "energyConsumption": 95,
                    "memorySize" : 32
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/ram/all")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Override
    String getPathOfEntity() {
        return "ram";
    }

    @Override
    String getIdOfEntity() {
        return "testRam1";
    }

    @Override
    String getJsonRequestBodyForUpdate() {
        return """
                {
                    "id": "%s",
                    "hardwareSpec": {
                        "name": "Updated RAM",
                        "description": "Updated description",
                        "price": 250,
                        "rating": 4.6
                    },
                    "type" : "ddr2",
                    "energyConsumption": 85,
                    "memorySize": 23
                }
                """;
    }

    @Override
    String getUpdatedNameOfEntity() {
        return "Updated RAM";
    }
}
