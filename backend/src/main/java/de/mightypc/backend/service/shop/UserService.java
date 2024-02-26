package de.mightypc.backend.service;

import de.mightypc.backend.exception.pc.HardwareNotFoundException;
import de.mightypc.backend.exception.shop.UserNotFoundException;
import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.shop.Order;
import de.mightypc.backend.model.shop.User;
import de.mightypc.backend.model.shop.UserResponse;
import de.mightypc.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }

    public List<Order> getAllOrdersOfUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User doesn't exist!"));

        return user.orders();
    }

    public List<PC> getAllPcsOfUser(String userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new UserNotFoundException("User doesn't exist!"));

        return user.userPcs();
    }

    public Order saveOrder(Order order){
        return order;
    }
}