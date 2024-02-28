package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.pc.specs.HDD;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.service.pc.hardware.HddService;
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
class HddControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private HddService hddService;

    @Test
    void getAllHdds_shouldReturnEmptyList_whenRepositoryIsEmpty() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void saveHdd_shouldReturnCreatedHdd() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/hardware/hdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "hardwareSpec": {
                                                "name": "Seagate Barracuda",
                                                "description": "Seagate Barracuda 2TB",
                                                "price": 54.99,
                                                "rating": 4.5
                                            },
                                            "capacity": 2000,
                                            "energyConsumption": 5
                                        }
                                        """
                        ))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Seagate Barracuda"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(5));
    }

    @DirtiesContext
    @Test
    void getAllHdds_shouldReturnListWithOneHdd_whenOneHddWasSavedInRepository() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Seagate Barracuda", "Seagate Barracuda 2TB", new BigDecimal("54.99"), 4.5f);
        HDD hdd = new HDD(hardwareSpec, 2000, 5);
        hddService.save(hdd);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                [{
                                    "id": "%s",
                                    "hardwareSpec": {
                                        "name": "Seagate Barracuda",
                                        "description": "Seagate Barracuda 2TB",
                                        "price": 54.99,
                                        "rating": 4.5
                                    },
                                    "capacity": 2000,
                                    "energyConsumption": 5
                                }]
                                """.formatted(hdd.id())
                ));
    }

    @DirtiesContext
    @Test
    void getHddById_shouldReturnHdd_whenHddExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Seagate Barracuda", "Seagate Barracuda 2TB", new BigDecimal("54.99"), 4.5f);
        HDD hdd = new HDD(hardwareSpec, 2000, 5);
        HDD savedHdd = hddService.save(hdd);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/" + savedHdd.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedHdd.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Seagate Barracuda"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(5));
    }

    @DirtiesContext
    @Test
    void deleteHddById_shouldDeleteHdd_whenHddExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Seagate Barracuda", "Seagate Barracuda 2TB", new BigDecimal("54.99"), 4.5f);
        HDD hdd = new HDD(hardwareSpec, 2000, 5);
        HDD savedHdd = hddService.save(hdd);

        mvc.perform(MockMvcRequestBuilders.delete("/api/hardware/hdd/" + savedHdd.id()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/hdd/" + savedHdd.id()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updateHdd_shouldUpdateHddDetails() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Seagate Barracuda", "Seagate Barracuda 2TB", new BigDecimal("54.99"), 4.5f);
        HDD hdd = new HDD(hardwareSpec, 2000, 5);
        HDD savedHdd = hddService.save(hdd);

        String updatedJson = String.format(
                """
                        {
                            "id": "%s",
                            "hardwareSpec": {
                                "name": "WD Blue",
                                "description": "WD Blue 1TB",
                                "price": 49.99,
                                "rating": 4.7
                            },
                            "capacity": 1000,
                            "energyConsumption": 4
                        }
                        """,
                savedHdd.id());

        mvc.perform(MockMvcRequestBuilders.put("/api/hardware/hdd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedHdd.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("WD Blue"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.capacity").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.energyConsumption").value(4));
    }
}
