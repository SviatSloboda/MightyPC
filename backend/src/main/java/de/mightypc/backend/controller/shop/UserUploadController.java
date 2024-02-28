package de.mightypc.backend.controller.shop;

import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.shop.UserService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user/upload/image/{userId}")
public class UserUploadController {
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    public UserUploadController(CloudinaryService cloudinaryService, UserService userService) {
        this.cloudinaryService = cloudinaryService;
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@RequestPart(name = "file") MultipartFile file,
                              @PathVariable String userId) throws IOException {
        String photoUrl = cloudinaryService.uploadFile(file, userId);
        userService.attachPhoto(userId, photoUrl);
        return photoUrl;
    }

    @DeleteMapping
    public void deleteImage(@PathVariable String userId) {
        userService.deleteImage(userId);
    }
}