package de.mightypc.backend.controller.hardware.upload;

import de.mightypc.backend.service.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class SsdUploadControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CloudinaryService cloudinaryService;

    @Test
    void uploadImage_WhenImageIsUploaded_ThenReturnPhotoUrl() throws Exception {
        String mockId = "1";
        String expectedPhotoUrl = "http://example.com/photo.jpg";
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "Test image content".getBytes());

        when(cloudinaryService.uploadFile(any(MultipartFile.class), any(String.class))).thenReturn(expectedPhotoUrl);

        mvc.perform(multipart("/api/ssd/upload/image/" + mockId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedPhotoUrl));
    }
}
