package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.specs.HardwareSpec;
import de.mightypc.backend.model.specs.PcCase;
import de.mightypc.backend.service.hardware.PcCaseService;
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
class PcCaseControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PcCaseService pcCaseService;

    @Test
    void getAllPcCases_shouldReturnEmptyList_whenRepositoryIsEmpty() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void savePcCase_shouldReturnCreatedPcCase() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/hardware/pc-case")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "hardwareSpec": {
                                                "name": "NZXT H510",
                                                "description": "Compact ATX Mid-Tower PC Gaming Case",
                                                "price": 69.99,
                                                "rating": 4.7
                                            },
                                            "dimensions": "428mm x 210mm x 460mm"
                                        }
                                        """
                        ))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("NZXT H510"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dimensions").value("428mm x 210mm x 460mm"));
    }

    @DirtiesContext
    @Test
    void getAllPcCases_shouldReturnListWithOnePcCase_whenOnePcCaseWasSavedInRepository() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("NZXT H510", "Compact ATX Mid-Tower PC Gaming Case", new BigDecimal("69.99"), 4.7f);
        PcCase pcCase = new PcCase(hardwareSpec, "428mm x 210mm x 460mm");
        pcCaseService.save(pcCase);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                [{
                                    "id": "%s",
                                    "hardwareSpec": {
                                        "name": "NZXT H510",
                                        "description": "Compact ATX Mid-Tower PC Gaming Case",
                                        "price": 69.99,
                                        "rating": 4.7
                                    },
                                    "dimensions": "428mm x 210mm x 460mm"
                                }]
                                """.formatted(pcCase.id())
                ));
    }

    @DirtiesContext
    @Test
    void getPcCaseById_shouldReturnPcCase_whenPcCaseExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("NZXT H510", "Compact ATX Mid-Tower PC Gaming Case", new BigDecimal("69.99"), 4.7f);
        PcCase pcCase = new PcCase(hardwareSpec, "428mm x 210mm x 460mm");
        PcCase savedPcCase = pcCaseService.save(pcCase);

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/" + savedPcCase.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedPcCase.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("NZXT H510"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dimensions").value("428mm x 210mm x 460mm"));
    }

    @DirtiesContext
    @Test
    void deletePcCaseById_shouldDeletePcCase_whenPcCaseExists() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("NZXT H510", "Compact ATX Mid-Tower PC Gaming Case", new BigDecimal("69.99"), 4.7f);
        PcCase pcCase = new PcCase(hardwareSpec, "428mm x 210mm x 460mm");
        PcCase savedPcCase = pcCaseService.save(pcCase);

        mvc.perform(MockMvcRequestBuilders.delete("/api/hardware/pc-case/" + savedPcCase.id()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/" + savedPcCase.id()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void updatePcCase_shouldUpdatePcCaseDetails() throws Exception {
        HardwareSpec hardwareSpec = new HardwareSpec("NZXT H510", "Compact ATX Mid-Tower PC Gaming Case", new BigDecimal("69.99"), 4.7f);
        PcCase pcCase = new PcCase(hardwareSpec, "428mm x 210mm x 460mm");
        PcCase savedPcCase = pcCaseService.save(pcCase);

        String updatedJson = String.format(
                """
                        {
                            "id": "%s",
                            "hardwareSpec": {
                                "name": "Corsair 4000D Airflow",
                                "description": "Mid-Tower ATX PC Case",
                                "price": 94.99,
                                "rating": 4.8
                            },
                            "dimensions": "453mm x 230mm x 466mm"
                        }
                        """,
                savedPcCase.id());

        mvc.perform(MockMvcRequestBuilders.put("/api/hardware/pc-case")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedPcCase.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("Corsair 4000D Airflow"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dimensions").value("453mm x 230mm x 466mm"));
    }
}
