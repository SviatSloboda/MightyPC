package de.mightypc.backend.controller.hardware.upload;

import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.MotherboardService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/motherboard/upload")
public class MotherboardUploadController {
    private final CloudinaryService cloudinaryService;
    private final MotherboardService motherboardService;

    public MotherboardUploadController(CloudinaryService cloudinaryService, MotherboardService motherboardService) {
        this.cloudinaryService = cloudinaryService;
        this.motherboardService = motherboardService;
    }

    @PostMapping("/image/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@RequestPart(name = "file") MultipartFile file,
                              @PathVariable String id) throws IOException {
        String photoUrl = cloudinaryService.uploadFile(file, id);
        motherboardService.attachPhoto(id, photoUrl);
        return photoUrl;
    }
}
