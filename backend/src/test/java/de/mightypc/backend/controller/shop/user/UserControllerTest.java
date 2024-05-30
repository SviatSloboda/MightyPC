package de.mightypc.backend.controller.shop.user;

import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        String requestBody = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @DirtiesContext
    @WithMockUser
    @Test
    void logoutUser_shouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(post("/api/logout"))
                .andExpect(status().isOk());
    }

    @DirtiesContext
    @WithMockUser
    @Test
    void registerUser_shouldRegisterUserSuccessfully() throws Exception {
        String requestBody = """
                {
                    "email": "newuser@example.com",
                    "password": "newpassword123"
                }
                """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @DirtiesContext
    @WithMockUser
    @Test
    void deleteUser_shouldDeleteUserSuccessfully() throws Exception {
        userRepository.save(new User().withId("test@example.com"));
        String userId = "test@example.com";

        mockMvc.perform(delete("/api/user/" + userId))
                .andExpect(status().isOk());
    }

    @DirtiesContext
    @Test
    void loginUser_shouldLoginSuccessfully() throws Exception {
        String requestBody = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @DirtiesContext
    @Test
    void loginUser_shouldReturnUnauthorizedOnBadCredentials() throws Exception {
        String requestBody = """
                {
                    "email": "wrong@example.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @DirtiesContext
    @WithMockUser
    @Test
    void getCurrentUser_shouldReturnUserSuccessfully() throws Exception {
        mockMvc.perform(get("/api/user/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").exists());
    }
}
