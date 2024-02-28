package de.mightypc.backend.service.shop;

import de.mightypc.backend.exception.shop.UserNotFoundException;
import de.mightypc.backend.model.shop.User;
import de.mightypc.backend.model.shop.UserResponse;
import de.mightypc.backend.repository.shop.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getLoggedInUser(OAuth2User user) {
        if (user == null) {
            return null;
        }

        String userEmail = user.getAttribute("email");

        if (userEmail == null || userEmail.isEmpty()) {
            return null;
        }

        boolean isReturningUser = userRepository.existsByEmail(userEmail.trim());

        if (!isReturningUser) {
            return new UserResponse(userRepository.save(new User(userEmail, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true, "default", ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)), "")));
        }

        return new UserResponse(userRepository.getUserByEmail(userEmail));
    }

    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("There is no such user"));
    }

    public void attachPhoto(String userId, String photoUrl) {
        User user = getUserById(userId);

        user.setUserPhoto(photoUrl);

        userRepository.save(user);
    }

    public void deleteImage(String userId) {
        User user = getUserById(userId);

        user.setUserPhoto("");

        userRepository.save(user);
    }

    public void deleteAccount(String userId) {
        User user = getUserById(userId);

        userRepository.delete(user);
    }
}
