package de.mightypc.backend.controller.hardware;

import de.mightypc.backend.security.SecurityConfig;
import org.hamcrest.Matchers;
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

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
abstract class BaseControllerTest {
    private final String pathOfEntity = getPathOfEntity();
    private final String idOfEntity = getIdOfEntity();
    private final String jsonRequestBodyForUpdate = getJsonRequestBodyForUpdate();
    private final String updatedNameOfEntity = getUpdatedNameOfEntity();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAlls_shouldReturnAlls() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + pathOfEntity))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    void getById_shouldReturn() throws Exception {
        String entityId = idOfEntity;
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + pathOfEntity + "/{id}", entityId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(entityId));
    }

    @Test
    void getAllsByPage_shouldReturnPages() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + pathOfEntity + "/page")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)));
    }

    @Test
    @WithMockUser
    void deleteById_shouldReturnSuccessStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/hardware/" + pathOfEntity + "/{id}", idOfEntity))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    void update_shouldReturnUpdated() throws Exception {
        String entityId = idOfEntity;
        String jsonRequestBody = jsonRequestBodyForUpdate.formatted(entityId);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/hardware/" + pathOfEntity)
                        .contentType("application/json")
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(entityId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hardwareSpec.name").value(updatedNameOfEntity));
    }

    @DirtiesContext
    @Test
    void getItems_shouldReturnItemsFilteredByPriceRange() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + getPathOfEntity() + "/filtered")
                        .param("page", "0")
                        .param("size", "8")
                        .param("lowestPrice", "100")
                        .param("highestPrice", "300"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.price").value(200));
    }

    @DirtiesContext
    @Test
    void getItems_shouldReturnItemsSortedByRatingDesc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + getPathOfEntity() + "/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "rating-desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.rating").value(4.5f));
    }

    @DirtiesContext
    @Test
    void getItems_shouldReturnAllItems_whenNoFiltersApplied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + getPathOfEntity() + "/filtered")
                        .param("page", "0")
                        .param("size", "8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)));
    }

    @DirtiesContext
    @Test
    void getItems_shouldReturnItemsSortedByPriceAsc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + getPathOfEntity() + "/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "price-asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.price").value(200));
    }

    @DirtiesContext
    @Test
    void getItems_shouldReturnItemsSortedByPriceDesc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + getPathOfEntity() + "/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "price-desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.price").value(350));
    }

    @DirtiesContext
    @Test
    void getItems_shouldReturnItemsSortedByRatingAsc() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + getPathOfEntity() + "/filtered")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "rating-asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].hardwareSpec.rating").value(4.0f));
    }


    abstract String getPathOfEntity();

    abstract String getIdOfEntity();

    abstract String getJsonRequestBodyForUpdate();

    abstract String getUpdatedNameOfEntity();
}
