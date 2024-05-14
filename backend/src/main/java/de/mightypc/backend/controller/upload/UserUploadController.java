package de.mightypc.backend.controller.upload;

import de.mightypc.backend.service.CloudinaryService;
import de.mightypc.backend.service.shop.user.UserService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        String photoUrl = cloudinaryService.uploadFile(file);
        userService.attachPhoto(userId, photoUrl);
        return photoUrl;
    }

    @DeleteMapping
    public void deleteImage(@PathVariable String userId) {
        userService.deleteImage(userId);
    }
}
