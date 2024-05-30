package de.mightypc.backend.controller.shop.user;

import de.mightypc.backend.model.shop.user.CreateUser;
import de.mightypc.backend.model.shop.user.UserResponse;
import de.mightypc.backend.service.shop.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/auth")
    public UserResponse getUser(@AuthenticationPrincipal OAuth2User user) {
       logger.warn(user.getAttributes().toString());

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
    public void setUserPassword(@PathVariable String userId, @RequestBody String password) {
        userService.setPassword(userId, password);
    }

    @PostMapping("/{userId}/change-password")
    public void changeUserPassword(@PathVariable String userId, @RequestBody String[] passwords) {
        userService.changeUserPassword(userId, passwords);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody Map<String, String> credentials, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.get("email"),
                            credentials.get("password")
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.getSession(true);

            return ResponseEntity.ok(getCurrentUser(authentication));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/current")
    public UserResponse getCurrentUser(Authentication authentication) {
        UserResponse userResponse = userService.getLoggedInUser(authentication);
        if (userResponse != null) {
            return userResponse;
        } else {
            throw new IllegalStateException("user can't be null");
        }
    }
}
