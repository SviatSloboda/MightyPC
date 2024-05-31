package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.CpuRepository;
import de.mightypc.backend.security.SecurityConfig;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.CpuService;
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
class CpuUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CpuRepository cpuRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private CpuService cpuService;

    private CPU testCpu;

    @BeforeEach
    void setup() throws IOException {
        testCpu = new CPU("cpuId", new HardwareSpec("testCpu", "High performance CPU", new BigDecimal("300"), 3.5f), 100, "AM4");
        cpuRepository.save(testCpu);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(cpuService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);
            CPU cpu = cpuRepository.findById(id).orElse(null);
            if (cpu != null) {
                ArrayList<String> photos = new ArrayList<>(cpu.cpuPhotos());
                photos.add(photoUrl);
                cpu = cpu.withCpuPhotos(photos);
                return cpuRepository.save(cpu);
            }
            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/cpu/upload/image/{id}", testCpu.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToCpu() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/hardware/cpu/upload/image/{id}", testCpu.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<CPU> updatedCpu = cpuRepository.findById(testCpu.id());
        assertTrue(updatedCpu.isPresent());
        assertTrue(updatedCpu.get().cpuPhotos().contains("http://example.com/test.jpg"));
    }
}
