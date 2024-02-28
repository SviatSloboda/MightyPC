package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.Motherboard;
import de.mightypc.backend.service.pc.hardware.MotherboardService;
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
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class MotherboardControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MotherboardService motherboardService;

    @Test
    void getAllMotherboards_shouldReturnEmptyList_whenRepositoryIsEmpty() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void saveMotherboard_shouldReturnCreatedMotherboard() throws Exception {
        String requestBody = """
                {
                    "hardwareSpec": {
                        "name": "ASUS ROG Maximus",
                        "description": "High-performance Z490 gaming motherboard",
                        "price": 250.00,
                        "rating": 4.8
                    },
                    "energyConsumption": 75,
                    "graphicCardCompatibility": ["NVIDIA RTX 3080", "AMD Radeon RX 6800"],
                    "processorCompatibility": ["Intel Core i9", "Intel Core i7"]
                }
                """;

        mvc.perform(MockMvcRequestBuilders.post("/api/hardware/motherboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("ASUS ROG Maximus"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(75))
                .andExpect(MockMvcResultMatchers.jsonPath("$.graphicCardCompatibility").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.processorCompatibility").isArray());
    }

    @DirtiesContext
    @Test
    void getAllMotherboards_shouldReturnListWithOneMotherboard_whenOneMotherboardWasSavedInRepository() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("ASUS ROG Maximus", "High-performance Z490 gaming motherboard", new BigDecimal("250.00"), 4.8f);
        String[] graphicCardCompatibility = {"NVIDIA RTX 3080", "AMD Radeon RX 6800"};
        String[] processorCompatibility = {"Intel Core i9", "Intel Core i7"};
        Motherboard motherboard = new Motherboard(UUID.randomUUID().toString(), hardwareSpec, 75, graphicCardCompatibility, processorCompatibility);
        motherboardService.save(motherboard);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                [{
                                    "id": "%s",
                                    "hardwareSpec": {
                                        "name": "ASUS ROG Maximus",
                                        "description": "High-performance Z490 gaming motherboard",
                                        "price": 250.00,
                                        "rating": 4.8
                                    },
                                    "energyConsumption": 75,
                                    "graphicCardCompatibility": ["NVIDIA RTX 3080", "AMD Radeon RX 6800"],
                                    "processorCompatibility": ["Intel Core i9", "Intel Core i7"]
                                }]
                                """.formatted(motherboard.id())
                ));
    }

    @DirtiesContext
    @Test
    void getMotherboardById_shouldReturnMotherboard_whenMotherboardExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("ASUS ROG Maximus", "High-performance Z490 gaming motherboard", new BigDecimal("250.00"), 4.8f);
        String[] graphicCardCompatibility = {"NVIDIA RTX 3080", "AMD Radeon RX 6800"};
        String[] processorCompatibility = {"Intel Core i9", "Intel Core i7"};
        Motherboard motherboard = new Motherboard(UUID.randomUUID().toString(), hardwareSpec, 75, graphicCardCompatibility, processorCompatibility);
        Motherboard savedMotherboard = motherboardService.save(motherboard);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/" + savedMotherboard.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedMotherboard.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("ASUS ROG Maximus"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(75))
                .andExpect(MockMvcResultMatchers.jsonPath("$.graphicCardCompatibility").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.processorCompatibility").isArray());
    }

    @DirtiesContext
    @Test
    void deleteMotherboardById_shouldDeleteMotherboard_whenMotherboardExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("ASUS ROG Maximus", "High-performance Z490 gaming motherboard", new BigDecimal("250.00"), 4.8f);
        String[] graphicCardCompatibility = {"NVIDIA RTX 3080", "AMD Radeon RX 6800"};
        String[] processorCompatibility = {"Intel Core i9", "Intel Core i7"};
        Motherboard motherboard = new Motherboard(UUID.randomUUID().toString(), hardwareSpec, 75, graphicCardCompatibility, processorCompatibility);
        Motherboard savedMotherboard = motherboardService.save(motherboard);

        mvc.perform(MockMvcRequestBuilders.delete("/api/hardware/motherboard/" + savedMotherboard.id()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/motherboard/" + savedMotherboard.id()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updateMotherboard_shouldUpdateMotherboardDetails() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("ASUS ROG Maximus", "High-performance Z490 gaming motherboard", new BigDecimal("250.00"), 4.8f);
        String[] graphicCardCompatibility = {"NVIDIA RTX 3080", "AMD Radeon RX 6800"};
        String[] processorCompatibility = {"Intel Core i9", "Intel Core i7"};
        Motherboard motherboard = new Motherboard(UUID.randomUUID().toString(), hardwareSpec, 75, graphicCardCompatibility, processorCompatibility);
        Motherboard savedMotherboard = motherboardService.save(motherboard);

        String updatedJson = String.format(
                """
                        {
                            "id": "%s",
                            "hardwareSpec": {
                                "name": "MSI MEG Z490 Godlike",
                                "description": "Ultimate Gaming Motherboard",
                                "price": 599.99,
                                "rating": 5.0
                            },
                            "energyConsumption": 80,
                            "graphicCardCompatibility": ["NVIDIA RTX 3090"],
                            "processorCompatibility": ["Intel Core i9"]
                        }
                        """,
                savedMotherboard.id());

        mvc.perform(MockMvcRequestBuilders.put("/api/hardware/motherboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedMotherboard.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("MSI MEG Z490 Godlike"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(80))
                .andExpect(MockMvcResultMatchers.jsonPath("$.graphicCardCompatibility").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.processorCompatibility").isArray());
    }
}
