package de.mightypc.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EntityNotFoundExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @DirtiesContext
    @Test
    void handleCpuNotFoundException_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/hardware/cpu/trigger-cpu-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timeStamp").exists());
    }

    @DirtiesContext
    @Test
    void handleGpuNotFoundException_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/hardware/gpu/trigger-gpu-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"))
                .andExpect(jsonPath("$.timeStamp").exists());
    }
}
