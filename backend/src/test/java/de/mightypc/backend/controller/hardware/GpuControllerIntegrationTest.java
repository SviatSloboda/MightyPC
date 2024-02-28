package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.pc.specs.GPU;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.service.pc.hardware.GpuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
class GpuControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GpuService gpuService;

    @Test
    void getAllGpus_shouldReturnEmptyList_whenRepositoryIsEmpty() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/gpu"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void saveGpu_shouldReturnCreatedGpu() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/hardware/gpu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "hardwareSpec": {
                                                "name": "NVIDIA RTX 3080",
                                                "description": "NVIDIA GeForce RTX 3080",
                                                "price": 699.99,
                                                "rating": 5.0
                                            },
                                            "performance": 8704,
                                            "energyConsumption": 320
                                        }
                                        """
                        ))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("NVIDIA RTX 3080"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.performance").value(8704))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(320));
    }

    @DirtiesContext
    @Test
    void getAllGpus_shouldReturnListWithOneGpu_whenOneGpuWasSavedInRepository() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("NVIDIA RTX 3080", "NVIDIA GeForce RTX 3080", new BigDecimal("699.99"), 5.0f);
        GPU gpu = new GPU(hardwareSpec, 8704, 320);
        gpuService.save(gpu);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/gpu"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                [{
                                    "id": "%s",
                                    "hardwareSpec": {
                                        "name": "NVIDIA RTX 3080",
                                        "description": "NVIDIA GeForce RTX 3080",
                                        "price": 699.99,
                                        "rating": 5.0
                                    },
                                    "performance": 8704,
                                    "energyConsumption": 320
                                }]
                                """.formatted(gpu.id())
                ));
    }

    @DirtiesContext
    @Test
    void getGpuById_shouldReturnGpu_whenGpuExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("NVIDIA RTX 3080", "NVIDIA GeForce RTX 3080", new BigDecimal("699.99"), 5.0f);
        GPU gpu = new GPU(hardwareSpec, 8704, 320);
        GPU savedGpu = gpuService.save(gpu);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/gpu/" + savedGpu.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedGpu.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("NVIDIA RTX 3080"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.performance").value(8704))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(320));
    }

    @DirtiesContext
    @Test
    void deleteGpuById_shouldDeleteGpu_whenGpuExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("NVIDIA RTX 3080", "NVIDIA GeForce RTX 3080", new BigDecimal("699.99"), 5.0f);
        GPU gpu = new GPU(hardwareSpec, 8704, 320);
        GPU savedGpu = gpuService.save(gpu);

        mvc.perform(MockMvcRequestBuilders.delete("/api/hardware/gpu/" + savedGpu.id()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/gpu/" + savedGpu.id()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updateGpu_shouldUpdateGpuDetails() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("NVIDIA RTX 3080", "NVIDIA GeForce RTX 3080", new BigDecimal("699.99"), 5.0f);
        GPU gpu = new GPU(hardwareSpec, 8704, 320);
        GPU savedGpu = gpuService.save(gpu);

        String updatedJson = String.format(
                """
                        {
                            "id": "%s",
                            "hardwareSpec": {
                                "name": "NVIDIA RTX 3090",
                                "description": "NVIDIA GeForce RTX 3090",
                                "price": 1499.99,
                                "rating": 5.0
                            },
                            "performance": 10496,
                            "energyConsumption": 350
                        }
                        """,
                savedGpu.id());

        mvc.perform(MockMvcRequestBuilders.put("/api/hardware/gpu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedGpu.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("NVIDIA RTX 3090"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.performance").value(10496))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(350));
    }
}
