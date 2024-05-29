package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.repository.pc.PcRepository;
import de.mightypc.backend.security.SecurityConfig;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.pc.PcService;
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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser
class PcUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PcRepository pcRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private PcService pcService;

    private PC testPC;

    @BeforeEach
    void setup() throws IOException {
        testPC = new PC(
                "testId",
                new HardwareSpec("TestPC", "Cool PC!", new BigDecimal(666), 2.33f),
                null,
                900,
                Collections.emptyList()
        );

        pcRepository.save(testPC);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(pcService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);

            PC pc = pcRepository.findById(id).orElse(null);

            if (pc != null) {
                ArrayList<String> photos = new ArrayList<>(pc.photos());
                photos.add(photoUrl);
                pc = pc.withPhotos(photos);

                return pcRepository.save(pc);
            }

            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/pc/upload/image/{id}", testPC.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToPC() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/pc/upload/image/{id}", testPC.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<PC> updatedPC = pcRepository.findById(testPC.id());
        assertTrue(updatedPC.isPresent());
        assertTrue(updatedPC.get().photos().contains("http://example.com/test.jpg"));
    }
}
