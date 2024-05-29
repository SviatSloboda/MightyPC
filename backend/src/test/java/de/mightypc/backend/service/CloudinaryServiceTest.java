package de.mightypc.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CloudinaryServiceTest {
    private final Cloudinary cloudinary = mock(Cloudinary.class);
    private final CloudinaryService cloudinaryService = new CloudinaryService(cloudinary);

    @Test
    void uploadFile_shouldReturnSecureUrl() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        File tempFile = File.createTempFile("file", null);

        when(file.getOriginalFilename()).thenReturn("testFile");
        doNothing().when(file).transferTo(tempFile);

        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(File.class), any(Map.class))).thenReturn(Map.of("secure_url", "http://example.com/testFile"));

        String actual = cloudinaryService.uploadFile(file);
        String expected = "http://example.com/testFile";

        assertEquals(expected, actual);
    }
}
