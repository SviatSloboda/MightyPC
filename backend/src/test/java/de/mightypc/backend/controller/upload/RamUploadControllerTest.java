package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.RamRepository;
import de.mightypc.backend.security.SecurityConfig;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.RamService;
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
class RamUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RamRepository ramRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private RamService ramService;

    private RAM testRam;

    @BeforeEach
    void setup() throws IOException {
        testRam = new RAM("ramId", new HardwareSpec("testRam", "High performance RAM", new BigDecimal("300"), 3.5f), "ddr3", 2, 2);
        ramRepository.save(testRam);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(ramService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);
            RAM ram = ramRepository.findById(id).orElse(null);
            if (ram != null) {
                ArrayList<String> photos = new ArrayList<>(ram.ramPhotos());
                photos.add(photoUrl);
                ram = ram.withRamPhotos(photos);
                return ramRepository.save(ram);
            }
            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/ram/upload/image/{id}", testRam.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToRam() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/ram/upload/image/{id}", testRam.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<RAM> updatedRam = ramRepository.findById(testRam.id());
        assertTrue(updatedRam.isPresent());
        assertTrue(updatedRam.get().ramPhotos().contains("http://example.com/test.jpg"));
    }
}
