package de.mightypc.backend.controller.upload.pc;

import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.pc.WorkstationService;
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
@RequestMapping("/api/workstation/upload")
public class WorkstationUploadController {
    private final CloudinaryService cloudinaryService;
    private final WorkstationService workstationService;

    public WorkstationUploadController(CloudinaryService cloudinaryService, WorkstationService workstationService) {
        this.cloudinaryService = cloudinaryService;
        this.workstationService = workstationService;
    }

    @PostMapping("/image/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@RequestPart(name = "file") MultipartFile file,
                              @PathVariable String id) throws IOException {
        String photoUrl = cloudinaryService.uploadFile(file);
        workstationService.attachPhoto(id, photoUrl);
        return photoUrl;
    }
}
