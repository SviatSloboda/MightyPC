package de.mightypc.backend.controller.shop.user;

import de.mightypc.backend.model.shop.user.CreateUser;
import de.mightypc.backend.model.shop.user.UserResponse;
import de.mightypc.backend.service.shop.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PostMapping("/logout")
    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        userService.logoutUser(request, response);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@RequestBody CreateUser createUser) {
        userService.registerUserWithEmailAndPassword(createUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteAccount(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) {
        logoutUser(request, response);

        userService.deleteAccount(userId);
    }

    @PostMapping("/{userId}/set-password")
    public void setUserPassword(@PathVariable String userId, @RequestBody String password){
        userService.setPassword(userId, password);
    }

    @PostMapping("/{userId}/change-password")
    public void changeUserPassword(@PathVariable String userId, @RequestBody String[] passwords){
        userService.changeUserPassword(userId, passwords);
    }
}
