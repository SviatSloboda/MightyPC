package de.mightypc.backend.controller.pc.upload;

import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.pc.hardware.PcCaseService;
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
@RequestMapping("/api/pc-case/upload")
public class PcCaseUploadController {
    private final CloudinaryService cloudinaryService;
    private final PcCaseService pcCaseService;

    public PcCaseUploadController(CloudinaryService cloudinaryService, PcCaseService pcCaseService) {
        this.cloudinaryService = cloudinaryService;
        this.pcCaseService = pcCaseService;
    }

    @PostMapping("/image/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@RequestPart(name = "file") MultipartFile file,
                              @PathVariable String id) throws IOException {
        String photoUrl = cloudinaryService.uploadFile(file, id);
        pcCaseService.attachPhoto(id, photoUrl);
        return photoUrl;
    }
}
