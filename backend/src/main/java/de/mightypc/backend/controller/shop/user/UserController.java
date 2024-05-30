package de.mightypc.backend.controller.shop.user;

import de.mightypc.backend.model.shop.user.CreateUser;
import de.mightypc.backend.model.shop.user.UserResponse;
import de.mightypc.backend.service.shop.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
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
