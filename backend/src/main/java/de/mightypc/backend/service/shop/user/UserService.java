package de.mightypc.backend.service.shop.user;

import de.mightypc.backend.exception.shop.user.UserNotFoundException;
import de.mightypc.backend.model.shop.user.CreateUser;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.model.shop.user.UserResponse;
import de.mightypc.backend.repository.shop.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse getLoggedInUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String userEmail = authentication.getName();
        if (userEmail == null || userEmail.isEmpty()) {
            return null;
        }
        boolean isReturningUser = userRepository.existsByEmail(userEmail.trim());
        if (!isReturningUser) {
            return new UserResponse(
                    userRepository.save(new User(
                            UUID.randomUUID().toString(),
                            userEmail,
                            "",
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            false,
                            "CUSTOMER",
                            ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)
                            ), "")
                    ));
        }
        return new UserResponse(userRepository.getUserByEmail(userEmail));
    }

    public UserResponse getLoggedInUser(OAuth2User oauth2User) {
        if (oauth2User == null) {
            return null;
        }
        String userEmail = oauth2User.getAttribute("email");
        if (userEmail == null || userEmail.isEmpty()) {
            return null;
        }
        boolean isReturningUser = userRepository.existsByEmail(userEmail.trim());
        if (!isReturningUser) {
            return new UserResponse(
                    userRepository.save(new User(
                            UUID.randomUUID().toString(),
                            userEmail,
                            "",
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            true,
                            "default",
                            ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)),
                            "")));
        }
        return new UserResponse(userRepository.getUserByEmail(userEmail));
    }

    public void registerUserWithEmailAndPassword(CreateUser createuser) {
        if (createuser == null) throw new IllegalStateException("User data can't be null!");

        boolean existsByEmail = userRepository.existsByEmail(createuser.email());
        if (existsByEmail)
            throw new IllegalStateException("User is already registered");

        userRepository.save(new User(
                UUID.randomUUID().toString(),
                createuser.email(),
                passwordEncoder.encode(createuser.password()),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                false,
                "CUSTOMER",
                ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)
                ), "")
        );
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

    public User attachPhoto(String userId, String photoUrl) {
        User user = getUserById(userId);
        user.setUserPhoto(photoUrl);
        return userRepository.save(user);
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

    public void setPassword(String userId, String password) {
        User user = getUserById(userId);

        if (!user.getPassword().isEmpty()) throw new IllegalStateException("Password is already set!!!");

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

    }

    public void changeUserPassword(String userId, String[] passwords) {
        User user = getUserById(userId);
        if (passwordEncoder.matches(passwords[0], user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwords[1]));
            userRepository.save(user);
        } else {
            throw new NoSuchElementException("Bad password!");
        }
    }
}
