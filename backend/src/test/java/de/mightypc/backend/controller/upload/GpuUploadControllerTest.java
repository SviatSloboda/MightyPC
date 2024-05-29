package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.GpuRepository;
import de.mightypc.backend.security.SecurityConfig;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.GpuService;
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
class GpuUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GpuRepository gpuRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private GpuService gpuService;

    private GPU testGpu;

    @BeforeEach
    void setup() throws IOException {
        testGpu = new GPU("gpuId", new HardwareSpec("testGpu", "High performance GPU", new BigDecimal("300"), 3.5f), 100);
        gpuRepository.save(testGpu);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(gpuService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);
            GPU gpu = gpuRepository.findById(id).orElse(null);
            if (gpu != null) {
                ArrayList<String> photos = new ArrayList<>(gpu.gpuPhotos());
                photos.add(photoUrl);
                gpu = gpu.withGpuPhotos(photos);
                return gpuRepository.save(gpu);
            }
            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/gpu/upload/image/{id}", testGpu.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToGpu() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/gpu/upload/image/{id}", testGpu.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<GPU> updatedGpu = gpuRepository.findById(testGpu.id());
        assertTrue(updatedGpu.isPresent());
        assertTrue(updatedGpu.get().gpuPhotos().contains("http://example.com/test.jpg"));
    }
}
