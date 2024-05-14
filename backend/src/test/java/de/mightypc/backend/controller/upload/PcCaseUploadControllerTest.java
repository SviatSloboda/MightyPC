package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.PcCaseService;
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
class PcCaseUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PcCaseRepository PcCaseRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private PcCaseService PcCaseService;

    private PcCase testPcCase;

    @BeforeEach
    void setup() throws IOException {
        testPcCase = new PcCase("PcCaseId", new HardwareSpec("testPcCase", "High performance PcCase", new BigDecimal("300"), 3.5f), "3x3x3");
        PcCaseRepository.save(testPcCase);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(PcCaseService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);
            PcCase PcCase = PcCaseRepository.findById(id).orElse(null);
            if (PcCase != null) {
                ArrayList<String> photos = new ArrayList<>(PcCase.pcCasePhotos());
                photos.add(photoUrl);
                PcCase = PcCase.withPcCasePhotos(photos);
                return PcCaseRepository.save(PcCase);
            }
            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/pc-case/upload/image/{id}", testPcCase.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToPcCase() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/pc-case/upload/image/{id}", testPcCase.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<PcCase> updatedPcCase = PcCaseRepository.findById(testPcCase.id());
        assertTrue(updatedPcCase.isPresent());
        assertTrue(updatedPcCase.get().pcCasePhotos().contains("http://example.com/test.jpg"));
    }
}
