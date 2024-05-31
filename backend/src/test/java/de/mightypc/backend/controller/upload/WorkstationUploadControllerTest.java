package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.pc.Workstation;
import de.mightypc.backend.repository.pc.WorkstationRepository;
import de.mightypc.backend.security.SecurityConfig;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.pc.WorkstationService;
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
@WithMockUser(roles = "ADMIN")
class WorkstationUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkstationRepository workstationRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private WorkstationService workstationService;

    private Workstation testWorkstation;

    @BeforeEach
    void setup() throws IOException {
        testWorkstation = new Workstation(
                "testId",
                new HardwareSpec("TestWorkstation", "Cool Workstation!", new BigDecimal(666), 2.33f),
                null,
                1,
                1,
                900,
                Collections.emptyList()
        );

        workstationRepository.save(testWorkstation);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(workstationService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);

            Workstation workstation = workstationRepository.findById(id).orElse(null);

            if (workstation != null) {
                ArrayList<String> photos = new ArrayList<>(workstation.photos());
                photos.add(photoUrl);
                workstation = workstation.withPhotos(photos);

                return workstationRepository.save(workstation);
            }

            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/workstation/upload/image/{id}", testWorkstation.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToWorkstation() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/workstation/upload/image/{id}", testWorkstation.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<Workstation> updatedWorkstation = workstationRepository.findById(testWorkstation.id());
        assertTrue(updatedWorkstation.isPresent());
        assertTrue(updatedWorkstation.get().photos().contains("http://example.com/test.jpg"));
    }
}
