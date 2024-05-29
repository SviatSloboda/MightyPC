package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.SsdRepository;
import de.mightypc.backend.security.SecurityConfig;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.SsdService;
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
import java.math.BigDecimal;
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
class SsdUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SsdRepository ssdRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private SsdService ssdService;

    private SSD testSsd;

    @BeforeEach
    void setup() throws IOException {
        testSsd = new SSD("ssdId", new HardwareSpec("testSsd", "High performance SSD", new BigDecimal("300"), 3.5f), 100, 32);
        ssdRepository.save(testSsd);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(ssdService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);

            SSD ssd = ssdRepository.findById(id).orElse(null);

            if (ssd != null) {
                ArrayList<String> photos = new ArrayList<>(ssd.ssdPhotos());
                photos.add(photoUrl);
                ssd = ssd.withSsdPhotos(photos);
                return ssdRepository.save(ssd);
            }

            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/ssd/upload/image/{id}", testSsd.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToSsd() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/ssd/upload/image/{id}", testSsd.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<SSD> updatedSsd = ssdRepository.findById(testSsd.id());
        assertTrue(updatedSsd.isPresent());
        assertTrue(updatedSsd.get().ssdPhotos().contains("http://example.com/test.jpg"));
    }
}
