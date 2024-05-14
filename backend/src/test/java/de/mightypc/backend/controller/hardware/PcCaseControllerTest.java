package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
class PcCaseControllerTest extends BaseControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private PcCaseRepository pcCaseRepository;

    private final PcCase testPcCase = new PcCase("testPcCase1", new HardwareSpec("testPcCase1", "testDescription", BigDecimal.valueOf(350), 4.5f), "2x2x2");
    private final PcCase testPcCase2 = new PcCase("testPcCase2", new HardwareSpec("testPcCase2", "testDescription", BigDecimal.valueOf(200), 4.0f), "1x1x1");


    @BeforeEach
    void setUp(){
        pcCaseRepository.save(testPcCase);
        pcCaseRepository.save(testPcCase2);
    }

    @DirtiesContext
    @Test
    void savePcCase_shouldReturnCreatedPcCase() throws Exception {
        String jsonRequestBody = """
                {
                    "hardwareSpec": {
                        "name": "testPcCase1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "dimensions": "1x1x1"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/pc-case")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value("testPcCase1"));
    }

    @DirtiesContext
    @Test
    void saveAllPcCases_shouldReturnStatusCreated() throws Exception {
        String jsonRequestBody = """
                [{
                    "hardwareSpec": {
                        "name": "testPcCase2",
                        "description": "testDescription",
                        "price": 200,
                        "rating": 4.0
                    },
                    "dimensions": "1x1x1"
                },
                {
                    "hardwareSpec": {
                        "name": "testPcCase1",
                        "description": "testDescription",
                        "price": 350,
                        "rating": 4.5
                    },
                    "dimensions": "1x1x1"
                }]
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/hardware/pc-case/all")
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @DirtiesContext
    @Test
    void getSortedPcCasesByPrice_shouldReturnSortedData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/sort/price?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testPcCase2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.name").value("testPcCase1"));
    }

    @DirtiesContext
    @Test
    void getSortedPcCasesByPrice_shouldReturnSortedDataInDescOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/sort/price?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testPcCase1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.name").value("testPcCase2"));
    }

    @DirtiesContext
    @Test
    void getSortedPcCasesByPrice_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/sort/price?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredPcCasesByPrice_shouldReturnFilteredData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/filter/price?lowest=250&highest=400"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.name").value("testPcCase1"));
    }

    @DirtiesContext
    @Test
    void getSortedPcCasesByRating_shouldReturnSortedDataAsc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/sort/rating?type=asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.rating").value(4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].hardwareSpec.rating").value(4.5));
    }

    @DirtiesContext
    @Test
    void getSortedPcCasesByRating_shouldReturnNotFoundWhenNoPcCasesExist() throws Exception {
        pcCaseRepository.deleteAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/sort/rating?type=desc"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DirtiesContext
    @Test
    void getSortedPcCasesByRating_shouldReturnBadRequest_whenRequestParamIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/sort/rating?type=badtype"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DirtiesContext
    @Test
    void getFilteredPcCasesByPrice_shouldReturnNotFoundWhenNoMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/pc-case/filter/price?lowest=500&highest=1000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
    }

    @Override
    String getPathOfEntity() {
        return "pc-case";
    }

    @Override
    String getIdOfEntity() {
        return "testPcCase1";
    }

    @Override
    String getJsonRequestBodyForUpdate() {
        return """
                {
                    "id": "%s",
                    "hardwareSpec": {
                        "name": "Updated PcCaSe",
                        "description": "Updated description",
                        "price": 250,
                        "rating": 4.6
                    },
                    "dimensions" : "3x2x2"
                }
                """;
    }

    @Override
    String getUpdatedNameOfEntity() {
        return "Updated PcCaSe";
    }
}
