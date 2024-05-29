package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.security.SecurityConfig;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.shop.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser
class UserUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setup() throws IOException {
        testUser = new User(
                "testId",
                "testEmail",
                "testPassword",
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                true,
                "user",
                "23.32",
                "link"
        );
        userRepository.save(testUser);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(userService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);

            User user = userRepository.findById(id).orElse(null);

            if (user != null) {
                user = user.withUserPhoto(photoUrl);

                return userRepository.save(user);
            }

            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/upload/image/{id}", testUser.getId())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToUser() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/upload/image/{id}", testUser.getId())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertTrue(updatedUser.get().getUserPhoto().contains("http://example.com/test.jpg"));
    }

    @Test
    void deleteImage_shouldDeletePhotoUrl() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/upload/image/testId"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
