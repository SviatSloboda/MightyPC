package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
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
class PcCaseControllerTest extends BaseControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PcCaseRepository pcCaseRepository;

    private final PcCase testPcCase = new PcCase("testPcCase1", new HardwareSpec("testPcCase1", "testDescription", BigDecimal.valueOf(350), 4.5f), "2x2x2");
    private final PcCase testPcCase2 = new PcCase("testPcCase2", new HardwareSpec("testPcCase2", "testDescription", BigDecimal.valueOf(200), 4.0f), "1x1x1");


    @BeforeEach
    void setUp(){
        pcCaseRepository.save(testPcCase);
        pcCaseRepository.save(testPcCase2);
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void savePcCase_shouldReturnCreatedPcCase() throws Exception {
        String jsonRequestBody = """
                {
                    "hardwareSpec": {
                        "name": "testPcCase1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "dimensions": "1x1x1"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/pc-case")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testPcCase1"));
    }

    @DirtiesContext
    @WithMockUser(roles = "ADMIN")
    @Test
    void saveAllPcCases_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "hardwareSpec": {
                        "name": "testPcCase2",
                        "description": "testDescription",
                        "price": 200,
                        "rating": 4.0
                    },
                    "dimensions": "1x1x1"
                },
                {
                    "hardwareSpec": {
                        "name": "testPcCase1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "dimensions": "1x1x1"
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/pc-case/all")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Override
    String getPathOfEntity() {
        return "pc-case";
    }

    @Override
    String getIdOfEntity() {
        return "testPcCase1";
    }

    @Override
    String getJsonRequestBodyForUpdate() {
        return """
                {
                    "id": "%s",
                    "hardwareSpec": {
                        "name": "Updated PcCaSe",
                        "description": "Updated description",
                        "price": 250,
                        "rating": 4.6
                    },
                    "dimensions" : "3x2x2"
                }
                """;
    }

    @Override
    String getUpdatedNameOfEntity() {
        return "Updated PcCaSe";
    }
}