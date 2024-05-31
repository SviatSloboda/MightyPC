package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.GpuRepository;
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
class GpuControllerTest extends BaseControllerTest{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private GpuRepository gpuRepository;

    private final GPU testGpu = new GPU("testGpu1",new HardwareSpec("testGpu1", "testDescription", BigDecimal.valueOf(350), 4.5f), 95);
    private final GPU testGpu2 = new GPU("testGpu2",new HardwareSpec("testGpu2", "testDescription", BigDecimal.valueOf(200), 4.0f), 65);

    @BeforeEach
    void setUp(){
        gpuRepository.save(testGpu);
        gpuRepository.save(testGpu2);
    }

    @DirtiesContext
    @Test
    @WithMockUser(roles = "ADMIN")
    void saveGpu_shouldReturnCreatedGpu() throws Exception {
        String jsonRequestBody = """
                {
                    "hardwareSpec": {
                        "name": "testGpu1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/gpu")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testGpu1"));
    }

    @DirtiesContext
    @Test
    @WithMockUser(roles = "ADMIN")
    void saveAllGpus_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "hardwareSpec": {
                        "name": "testGpu2",
                        "description": "testDescription",
                        "price": 200,
                        "rating": 4.0
                    },
                    "energyConsumption": 65
                },
                {
                    "hardwareSpec": {
                        "name": "testGpu1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "energyConsumption": 95
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/gpu/all")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Override
    String getPathOfEntity() {
        return "gpu";
    }

    @Override
    String getIdOfEntity() {
        return "testGpu1";
    }

    @Override
    String getJsonRequestBodyForUpdate() {
        return """
                {
                    "id": "%s",
                    "hardwareSpec": {
                        "name": "Updated GPU",
                        "description": "Updated description",
                        "price": 250,
                        "rating": 4.6
                    },
                    "energyConsumption": 85
                }
                """;
    }

    @Override
    String getUpdatedNameOfEntity() {
        return "Updated GPU";
    }
}
