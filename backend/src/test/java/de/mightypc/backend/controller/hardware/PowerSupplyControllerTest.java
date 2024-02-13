package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.PowerSupply;
import de.mightypc.backend.service.hardware.PowerSupplyService;
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
class PowerSupplyControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PowerSupplyService powerSupplyService;

    @Test
    void getAllPowerSupplies_shouldReturnEmptyList_whenRepositoryIsEmpty() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/psu"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void savePowerSupply_shouldReturnCreatedPowerSupply() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/hardware/psu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "hardwareSpec": {
                                                "name": "Corsair RM750x",
                                                "description": "750 Watt 80 PLUS Gold Certified Fully Modular PSU",
                                                "price": 129.99,
                                                "rating": 4.8
                                            },
                                            "power": 750
                                        }
                                        """
                        ))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Corsair RM750x"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.power").value(750));
    }

    @DirtiesContext
    @Test
    void getAllPowerSupplies_shouldReturnListWithOnePowerSupply_whenOnePowerSupplyWasSavedInRepository() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Corsair RM750x", "750 Watt 80 PLUS Gold Certified Fully Modular PSU", new BigDecimal("129.99"), 4.8f);
        PowerSupply powerSupply = new PowerSupply(hardwareSpec, 750);
        powerSupplyService.save(powerSupply);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/psu"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                [{
                                    "id": "%s",
                                    "hardwareSpec": {
                                        "name": "Corsair RM750x",
                                        "description": "750 Watt 80 PLUS Gold Certified Fully Modular PSU",
                                        "price": 129.99,
                                        "rating": 4.8
                                    },
                                    "power": 750
                                }]
                                """.formatted(powerSupply.id())
                ));
    }

    @DirtiesContext
    @Test
    void getPowerSupplyById_shouldReturnPowerSupply_whenPowerSupplyExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Corsair RM750x", "750 Watt 80 PLUS Gold Certified Fully Modular PSU", new BigDecimal("129.99"), 4.8f);
        PowerSupply powerSupply = new PowerSupply(hardwareSpec, 750);
        PowerSupply savedPowerSupply = powerSupplyService.save(powerSupply);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/psu/" + savedPowerSupply.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedPowerSupply.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Corsair RM750x"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.power").value(750));
    }

    @DirtiesContext
    @Test
    void deletePowerSupplyById_shouldDeletePowerSupply_whenPowerSupplyExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Corsair RM750x", "750 Watt 80 PLUS Gold Certified Fully Modular PSU", new BigDecimal("129.99"), 4.8f);
        PowerSupply powerSupply = new PowerSupply(hardwareSpec, 750);
        PowerSupply savedPowerSupply = powerSupplyService.save(powerSupply);

        mvc.perform(MockMvcRequestBuilders.delete("/api/hardware/psu/" + savedPowerSupply.id()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/psu/" + savedPowerSupply.id()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updatePowerSupply_shouldUpdatePowerSupplyDetails() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("Corsair RM750x", "750 Watt 80 PLUS Gold Certified Fully Modular PSU", new BigDecimal("129.99"), 4.8f);
        PowerSupply powerSupply = new PowerSupply(hardwareSpec, 750);
        PowerSupply savedPowerSupply = powerSupplyService.save(powerSupply);

        String updatedJson = String.format(
                """
                        {
                            "id": "%s",
                            "hardwareSpec": {
                                "name": "EVGA SuperNOVA 850 G5",
                                "description": "850 Watt 80 Plus Gold Fully Modular PSU",
                                "price": 159.99,
                                "rating": 4.9
                            },
                            "power": 850
                        }
                        """,
                savedPowerSupply.id());

        mvc.perform(MockMvcRequestBuilders.put("/api/hardware/psu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedPowerSupply.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("EVGA SuperNOVA 850 G5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.power").value(850));
    }
}
