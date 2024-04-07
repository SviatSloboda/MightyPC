package de.mightypc.backend.controller.pc.upload;

import de.mightypc.backend.service.CloudinaryService;

import de.mightypc.backend.service.pc.PcService;
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
@RequestMapping("/api/pc/upload")
public class PcUploadController {
    private final CloudinaryService cloudinaryService;
    private final PcService pcService;

    public PcUploadController(CloudinaryService cloudinaryService, PcService pcService) {
        this.cloudinaryService = cloudinaryService;
        this.pcService = pcService;
    }

    @PostMapping("/image/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@RequestPart(name = "file") MultipartFile file,
                              @PathVariable String id) throws IOException {
        String photoUrl = cloudinaryService.uploadFile(file, id);
        pcService.attachPhoto(id, photoUrl);
        return photoUrl;
    }
}