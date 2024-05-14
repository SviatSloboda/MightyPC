package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.MotherboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class MotherboardUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MotherboardRepository motherboardRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private MotherboardService motherboardService;

    private Motherboard testMotherboard;

    @BeforeEach
    void setup() throws IOException {
        testMotherboard = new Motherboard("motherboardId", new HardwareSpec("testMotherboard", "High performance Motherboard", new BigDecimal("300"), 3.5f), 100, "Am4");
        motherboardRepository.save(testMotherboard);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(motherboardService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);
            Motherboard motherboard = motherboardRepository.findById(id).orElse(null);
            if (motherboard != null) {
                ArrayList<String> photos = new ArrayList<>(motherboard.motherboardPhotos());
                photos.add(photoUrl);
                motherboard = motherboard.withMotherboardPhotos(photos);
                return motherboardRepository.save(motherboard);
            }
            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/motherboard/upload/image/{id}", testMotherboard.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToMotherboard() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/motherboard/upload/image/{id}", testMotherboard.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<Motherboard> updatedMotherboard = motherboardRepository.findById(testMotherboard.id());
        assertTrue(updatedMotherboard.isPresent());
        assertTrue(updatedMotherboard.get().motherboardPhotos().contains("http://example.com/test.jpg"));
    }
}
