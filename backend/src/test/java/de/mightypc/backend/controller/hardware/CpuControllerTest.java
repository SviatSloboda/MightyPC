package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.security.SecurityConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
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
    @WithMockUser
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
    @WithMockUser
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

    @DirtiesContext
    @Test
    @WithMockUser
    void getCpus_shouldReturnFilteredAndSortedCpus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "price-asc")
                        .param("socket", "LGA1151"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].socket").value("LGA1151"));
    }

    @DirtiesContext
    @Test
    void getSocketOfCpu_shouldReturnSocket() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/socket/{cpuId}", testCpu.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("LGA1151"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/socket/{cpuId}", testCpu2.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("AM4"));
    }

    @Override
    String getUpdatedNameOfEntity() {
        return "Updated CPU";
    }
}
