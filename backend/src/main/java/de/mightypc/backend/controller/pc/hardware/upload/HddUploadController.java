package de.mightypc.backend.controller.pc.hardware.upload;

import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.HddService;
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
@RequestMapping("/api/hdd/upload")
public class HddUploadController {
    private final CloudinaryService cloudinaryService;
    private final HddService hddService;

    public HddUploadController(CloudinaryService cloudinaryService, HddService hddService) {
        this.cloudinaryService = cloudinaryService;
        this.hddService = hddService;
    }

    @PostMapping("/image/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@RequestPart(name = "file") MultipartFile file,
                              @PathVariable String id) throws IOException {
        String photoUrl = cloudinaryService.uploadFile(file, id);
        hddService.attachPhoto(id, photoUrl);
        return photoUrl;
    }
}
