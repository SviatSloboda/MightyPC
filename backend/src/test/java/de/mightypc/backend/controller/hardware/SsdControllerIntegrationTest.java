package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.SSD;
import de.mightypc.backend.service.hardware.SsdService;
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
class SsdControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SsdService ssdService;

    @Test
    void getAllSsds_shouldReturnEmptyList_whenRepositoryIsEmpty() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void saveSsd_shouldReturnCreatedSsd() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/hardware/ssd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "hardwareSpec": {
                                                "name": "Samsung 970 EVO Plus",
                                                "description": "1TB NVMe M.2 SSD",
                                                "price": 169.99,
                                                "rating": 4.9
                                            },
                                            "capacity": 5,
                                            "energyConsumption": 5
                                        }
                                        """
                        ))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Samsung 970 EVO Plus"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(5));
    }

    @DirtiesContext
    @Test
    void getAllSsds_shouldReturnListWithOneSsd_whenOneSsdWasSavedInRepository() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Samsung 970 EVO Plus", "1TB NVMe M.2 SSD", new BigDecimal("169.99"), 4.9f);
        SSD ssd = new SSD(hardwareSpec, 5, 5);
        ssdService.save(ssd);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                [{
                                    "id": "%s",
                                    "hardwareSpec": {
                                        "name": "Samsung 970 EVO Plus",
                                        "description": "1TB NVMe M.2 SSD",
                                        "price": 169.99,
                                        "rating": 4.9
                                    },
                                    "capacity": 5,
                                    "energyConsumption": 5
                                }]
                                """.formatted(ssd.id())
                ));
    }

    @DirtiesContext
    @Test
    void getSsdById_shouldReturnSsd_whenSsdExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Samsung 970 EVO Plus", "1TB NVMe M.2 SSD", new BigDecimal("169.99"), 4.9f);
        SSD ssd = new SSD(hardwareSpec,5, 5);
        SSD savedSsd = ssdService.save(ssd);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/" + savedSsd.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedSsd.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Samsung 970 EVO Plus"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(5));
    }

    @DirtiesContext
    @Test
    void deleteSsdById_shouldDeleteSsd_whenSsdExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Samsung 970 EVO Plus", "1TB NVMe M.2 SSD", new BigDecimal("169.99"), 4.9f);
        SSD ssd = new SSD(hardwareSpec,5, 5);
        SSD savedSsd = ssdService.save(ssd);

        mvc.perform(MockMvcRequestBuilders.delete("/api/hardware/ssd/" + savedSsd.id()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/ssd/" + savedSsd.id()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updateSsd_shouldUpdateSsdDetails() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Samsung 970 EVO Plus", "1TB NVMe M.2 SSD", new BigDecimal("169.99"), 4.9f);
        SSD ssd = new SSD(hardwareSpec,5, 5);
        SSD savedSsd = ssdService.save(ssd);

        String updatedJson = String.format(
                """
                        {
                            "id": "%s",
                            "hardwareSpec": {
                                "name": "WD Blue SN550",
                                "description": "1TB NVMe Internal SSD",
                                "price": 99.99,
                                "rating": 4.8
                            },
                            "capacity": 4,
                            "energyConsumption": 4
                        }
                        """,
                savedSsd.id());

        mvc.perform(MockMvcRequestBuilders.put("/api/hardware/ssd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedSsd.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("WD Blue SN550"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(4));
    }
}
