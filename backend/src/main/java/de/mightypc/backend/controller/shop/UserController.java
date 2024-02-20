package de.mightypc.backend.controller.shop;

import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.shop.Order;
import de.mightypc.backend.model.shop.UserResponse;
import de.mightypc.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{userId}/orders")
    public List<Order> getAllOrdersOfUser(@PathVariable String userId) {
        return userService.getAllOrdersOfUser(userId);
    }

    @GetMapping("/{userID}/configurations")
    public List<PC> getAllPcsOfUser(@PathVariable String userID){
        return userService.getAllPcsOfUser(userID);
    }
}