package de.mightypc.backend.controller.upload;

import de.mightypc.backend.model.hardware.PowerSupply;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.PowerSupplyService;
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
class PowerSupplyUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PowerSupplyRepository powerSupplyRepository;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private PowerSupplyService powerSupplyService;

    private PowerSupply testPowerSupply;

    @BeforeEach
    void setup() throws IOException {
        testPowerSupply = new PowerSupply("powerSupplyId", new HardwareSpec("testPowerSupply", "High performance PowerSupply", new BigDecimal("300"), 3.5f), 100);
        powerSupplyRepository.save(testPowerSupply);

        when(cloudinaryService.uploadFile(any())).thenReturn("http://example.com/test.jpg");
        when(powerSupplyService.attachPhoto(anyString(), anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            String photoUrl = invocation.getArgument(1);
            PowerSupply powerSupply = powerSupplyRepository.findById(id).orElse(null);
            if (powerSupply != null) {
                ArrayList<String> photos = new ArrayList<>(powerSupply.powerSupplyPhotos());
                photos.add(photoUrl);
                powerSupply = powerSupply.withPowerSupplyPhotos(photos);
                return powerSupplyRepository.save(powerSupply);
            }
            return null;
        });
    }

    @Test
    void uploadImage_shouldReturnUploadedPhotoUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/psu/upload/image/{id}", testPowerSupply.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("http://example.com/test.jpg"));
    }

    @Test
    void uploadImage_shouldSavePhotoUrlToPowerSupply() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/psu/upload/image/{id}", testPowerSupply.id())
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Optional<PowerSupply> updatedPowerSupply = powerSupplyRepository.findById(testPowerSupply.id());
        assertTrue(updatedPowerSupply.isPresent());
        assertTrue(updatedPowerSupply.get().powerSupplyPhotos().contains("http://example.com/test.jpg"));
    }
}
