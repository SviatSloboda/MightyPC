package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.service.hardware.CpuService;
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
class CpuControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CpuService cpuService;

    @Test
    void getAllCpus_shouldReturnEmptyList_whenRepositoryIsEmpty() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void saveCpu_shouldReturnCreatedCpu() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/hardware/cpu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "hardwareSpec": {
                                                "name": "Ryzen 5",
                                                "description": "AMD Ryzen 5 Processor",
                                                "price": 199.99,
                                                "rating": 4.5
                                            },
                                            "performance": 3400,
                                            "energyConsumption": 65
                                        }
                                        """
                        ))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Ryzen 5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.performance").value(3400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(65));
    }

    @DirtiesContext
    @Test
    void getAllCpus_shouldReturnListWithOneCpu_whenOneCpuWasSavedInRepository() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Ryzen 5", "AMD Ryzen 5 Processor", new BigDecimal("199.99"), 4.5f);
        CPU cpu = new CPU("1",hardwareSpec, 3400, 65);
        cpuService.save(cpu);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                [{
                                    "id": "1",
                                    "hardwareSpec": {
                                        "name": "Ryzen 5",
                                        "description": "AMD Ryzen 5 Processor",
                                        "price": 199.99,
                                        "rating": 4.5
                                    },
                                    "performance": 3400,
                                    "energyConsumption": 65
                                }]
                                """
                ));
    }

    @DirtiesContext
    @Test
    void getCpuById_shouldReturnCpu_whenCpuExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Ryzen 5", "AMD Ryzen 5 Processor", new BigDecimal("199.99"), 4.5f);
        CPU cpu = new CPU(hardwareSpec, 3400, 65);
        CPU savedCpu = cpuService.save(cpu);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/" + savedCpu.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedCpu.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Ryzen 5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.performance").value(3400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(65));
    }

    @DirtiesContext
    @Test
    void deleteCpuById_shouldDeleteCpu_whenCpuExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Ryzen 5", "AMD Ryzen 5 Processor", new BigDecimal("199.99"), 4.5f);
        CPU cpu = new CPU(hardwareSpec, 3400, 65);
        CPU savedCpu = cpuService.save(cpu);

        mvc.perform(MockMvcRequestBuilders.delete("/api/hardware/cpu/" + savedCpu.id()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/cpu/" + savedCpu.id()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updateCpu_shouldUpdateCpuDetails() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Ryzen 5", "AMD Ryzen 5 Processor", new BigDecimal("199.99"), 4.5f);
        CPU cpu = new CPU(hardwareSpec, 3400, 65);
        CPU savedCpu = cpuService.save(cpu);

        String updatedJson = String.format(
                """
                        {
                            "id": "%s",
                            "hardwareSpec": {
                                "name": "Ryzen 7",
                                "description": "AMD Ryzen 7 Processor",
                                "price": 329.99,
                                "rating": 4.8
                            },
                            "performance": 3800,
                            "energyConsumption": 95
                        }
                        """,
                savedCpu.id());

        mvc.perform(MockMvcRequestBuilders.put("/api/hardware/cpu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedCpu.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Ryzen 7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.performance").value(3800))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(95));
    }
}
