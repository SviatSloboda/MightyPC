package de.mightypc.backend.service.shop.user;

import de.mightypc.backend.exception.shop.user.UserNotFoundException;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class UserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserService userService = new UserService(userRepository, passwordEncoder);

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                "testId",
                "testEmail",
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                true,
                "user",
                "23.32",
                "link"
        );
    }

    @Test
    void getLoggedInUser() {
    }

    @Test
    void logoutUser() {
    }

    @Test
    void getUserById_whenThereIsNoSuchUser_shouldThrowUserNotFoundException() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("testId"));
    }

    @Test
    void getUserById_shouldReturnProperUser() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        User actual = userService.getUserById("testId");

        // Assert
        verify(userRepository).findById("testId");
        assertEquals(user, actual);
    }

    @Test
    void attachPhoto() {
        // Arrange
        String testPhoto = "testPhoto";
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        userService.attachPhoto("testId", testPhoto);

        // Assert
        verify(userRepository).findById("testId");
        assertEquals(user.getUserPhoto(), testPhoto);
    }

    @Test
    void deleteImage() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        userService.deleteImage("testId");

        // Assert
        verify(userRepository).findById("testId");
        assertTrue(user.getUserPhoto().isEmpty());
    }

    @Test
    void deleteAccount() {
        // Arrange
        when(userRepository.findById("testId")).thenReturn(Optional.of(user));

        // Act
        userService.deleteAccount("testId");

        // Assert
        verify(userRepository).findById("testId");
        verify(userRepository).delete(user);
    }
}