package de.mightypc.backend.service;

import de.mightypc.backend.model.shop.User;
import de.mightypc.backend.model.shop.UserResponse;
import de.mightypc.backend.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

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
            return new UserResponse(userRepository.save(new User(userEmail, new ArrayList<>(), new ArrayList<>(), true)));
        }

        return new UserResponse(userRepository.getUserByEmail(userEmail));
    }
}