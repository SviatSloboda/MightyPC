package de.mightypc.backend.controller.hardware;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
abstract class BaseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final String pathOfEntity = getPathOfEntity();
    private final String idOfEntity = getIdOfEntity();
    private final String jsonRequestBodyForUpdate = getJsonRequestBodyForUpdate();
    private final String updatedNameOfEntity = getUpdatedNameOfEntity();

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
    void getAllsByPage_shouldReturnPageds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + pathOfEntity + "/page")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(2)));
    }

    @Test
    void deleteById_shouldReturnSuccessStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/hardware/" + pathOfEntity + "/{id}", idOfEntity))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
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

    @Test
    void getAllNames_shouldReturnNames() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hardware/" + pathOfEntity + "/names"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isMap());
    }

    abstract String getPathOfEntity();

    abstract String getIdOfEntity();

    abstract String getJsonRequestBodyForUpdate();

    abstract String getUpdatedNameOfEntity();
}