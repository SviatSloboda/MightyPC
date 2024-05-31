package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.HddRepository;
import de.mightypc.backend.security.SecurityConfig;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.HddService;
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
@WithMockUser(roles = "ADMIN")
class HddUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HddRepository hddRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private HddService hddService;

    private HDD testHdd;

    @BeforeEach
    void setup() throws IOException {
        testHdd = new HDD("hddId", new HardwareSpec("testHdd", "High performance HDD", new BigDecimal("300"), 3.5f), 100, 100);
        hddRepository.save(testHdd);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(hddService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);
            HDD hdd = hddRepository.findById(id).orElse(null);
            if (hdd != null) {
                ArrayList<String> photos = new ArrayList<>(hdd.hddPhotos());
                photos.add(photoUrl);
                hdd = hdd.withHddPhotos(photos);
                return hddRepository.save(hdd);
            }
            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/hdd/upload/image/{id}", testHdd.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToHdd() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/hdd/upload/image/{id}", testHdd.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<HDD> updatedHdd = hddRepository.findById(testHdd.id());
        assertTrue(updatedHdd.isPresent());
        assertTrue(updatedHdd.get().hddPhotos().contains("http://example.com/test.jpg"));
    }
}
