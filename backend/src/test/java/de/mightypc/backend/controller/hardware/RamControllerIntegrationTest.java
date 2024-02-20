package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.RAM;
import de.mightypc.backend.service.hardware.RamService;
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
class RamControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RamService ramService;

    @Test
    void getAllRams_shouldReturnEmptyList_whenRepositoryIsEmpty() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/ram"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void saveRam_shouldReturnCreatedRam() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/hardware/ram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "hardwareSpec": {
                                                "name": "Corsair Vengeance LPX",
                                                "description": "16GB (2x8GB) DDR4 DRAM 3200MHz C16",
                                                "price": 79.99,
                                                "rating": 4.8
                                            },
                                            "type": "DDR4",
                                            "energyConsumption": 3,
                                            "memorySize": 16
                                        }
                                        """
                        ))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Corsair Vengeance LPX"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("DDR4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memorySize").value(16));
    }

    @DirtiesContext
    @Test
    void getAllRams_shouldReturnListWithOneRam_whenOneRamWasSavedInRepository() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Corsair Vengeance LPX", "16GB (2x8GB) DDR4 DRAM 3200MHz C16", new BigDecimal("79.99"), 4.8f);
        RAM ram = new RAM(hardwareSpec, "DDR4", 3, 16);
        ramService.save(ram);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/ram"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                [{
                                    "id": "%s",
                                    "hardwareSpec": {
                                        "name": "Corsair Vengeance LPX",
                                        "description": "16GB (2x8GB) DDR4 DRAM 3200MHz C16",
                                        "price": 79.99,
                                        "rating": 4.8
                                    },
                                    "type": "DDR4",
                                    "energyConsumption": 3,
                                    "memorySize": 16
                                }]
                                """.formatted(ram.id())
                ));
    }

    @DirtiesContext
    @Test
    void getRamById_shouldReturnRam_whenRamExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Corsair Vengeance LPX", "16GB (2x8GB) DDR4 DRAM 3200MHz C16", new BigDecimal("79.99"), 4.8f);
        RAM ram = new RAM(hardwareSpec, "DDR4", 3, 16);
        RAM savedRam = ramService.save(ram);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/ram/" + savedRam.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedRam.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Corsair Vengeance LPX"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("DDR4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memorySize").value(16));
    }

    @DirtiesContext
    @Test
    void deleteRamById_shouldDeleteRam_whenRamExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Corsair Vengeance LPX", "16GB (2x8GB) DDR4 DRAM 3200MHz C16", new BigDecimal("79.99"), 4.8f);
        RAM ram = new RAM(hardwareSpec, "DDR4", 3, 16);
        RAM savedRam = ramService.save(ram);

        mvc.perform(MockMvcRequestBuilders.delete("/api/hardware/ram/" + savedRam.id()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/ram/" + savedRam.id()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updateRam_shouldUpdateRamDetails() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Corsair Vengeance LPX", "16GB (2x8GB) DDR4 DRAM 3200MHz C16", new BigDecimal("79.99"), 4.8f);
        RAM ram = new RAM(hardwareSpec, "DDR4", 3, 16);
        RAM savedRam = ramService.save(ram);

        String updatedJson = String.format(
                """
                        {
                            "id": "%s",
                            "hardwareSpec": {
                                "name": "G.SKILL Trident Z Royal",
                                "description": "16GB (2x8GB) DDR4 3600MHz C18",
                                "price": 129.99,
                                "rating": 4.9
                            },
                            "type": "DDR4",
                            "energyConsumption": 4,
                            "memorySize": 16
                        }
                        """,
                savedRam.id());

        mvc.perform(MockMvcRequestBuilders.put("/api/hardware/ram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedRam.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("G.SKILL Trident Z Royal"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("DDR4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.memorySize").value(16));
    }
}
