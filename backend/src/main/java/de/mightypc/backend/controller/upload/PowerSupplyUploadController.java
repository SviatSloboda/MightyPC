package de.mightypc.backend.controller.upload;

import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.hardware.PowerSupplyService;
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
@RequestMapping("/api/psu/upload")
public class PowerSupplyUploadController {
    private final CloudinaryService cloudinaryService;
    private final PowerSupplyService powerSupplyService;

    public PowerSupplyUploadController(CloudinaryService cloudinaryService, PowerSupplyService powerSupplyService) {
        this.cloudinaryService = cloudinaryService;
        this.powerSupplyService = powerSupplyService;
    }

    @PostMapping("/image/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@RequestPart(name = "file") MultipartFile file,
                              @PathVariable String id) throws IOException {
        String photoUrl = cloudinaryService.uploadFile(file);
        powerSupplyService.attachPhoto(id, photoUrl);
        return photoUrl;
    }
}
