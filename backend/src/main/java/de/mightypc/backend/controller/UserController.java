package de.mightypc.backend.controller;

import de.mightypc.backend.model.shop.UserResponse;
import de.mightypc.backend.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserResponse getUser(@AuthenticationPrincipal OAuth2User user) {
        return userService.getLoggedInUser(user);
    }
}