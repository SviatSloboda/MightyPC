package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.PowerSupply;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
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
class PowerSupplyControllerTest extends BaseControllerTest{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PowerSupplyRepository powerSupplyRepository;

    private final PowerSupply testPowerSupply = new PowerSupply("testPsu1", new HardwareSpec("testPowerSupply1", "testDescription", BigDecimal.valueOf(350), 4.5f), 95);
    private final PowerSupply testPowerSupply2 = new PowerSupply("testPsu2",  new HardwareSpec("testPowerSupply2", "testDescription", BigDecimal.valueOf(200), 4.0f), 65);

    @BeforeEach
    void setUp(){
        powerSupplyRepository.save(testPowerSupply);
        powerSupplyRepository.save(testPowerSupply2);
    }

    @DirtiesContext
    @WithMockUser
    @Test
    void savePowerSupply_shouldReturnCreatedPowerSupply() throws Exception {
        String jsonRequestBody = """
                {
                    "hardwareSpec": {
                        "name": "testPowerSupply1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "power": 95
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/psu")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testPowerSupply1"));
    }

    @DirtiesContext
    @WithMockUser
    @Test
    void saveAllPowerSupplies_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "hardwareSpec": {
                        "name": "testPowerSupply2",
                        "description": "testDescription",
                        "price": 200,
                        "rating": 4.0
                    },
                    "power": 65
                },
                {
                    "hardwareSpec": {
                        "name": "testPowerSupply1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "power": 95
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/psu/all")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Override
    String getPathOfEntity() {
        return "psu";
    }

    @Override
    String getIdOfEntity() {
        return "testPsu1";
    }

    @Override
    String getJsonRequestBodyForUpdate() {
        return """
                {
                    "id": "%s",
                    "hardwareSpec": {
                        "name": "Updated PSU",
                        "description": "Updated description",
                        "price": 250,
                        "rating": 4.6
                    },
                    "power" : 32
                }
                """;
    }

    @Override
    String getUpdatedNameOfEntity() {
        return "Updated PSU";
    }
}
