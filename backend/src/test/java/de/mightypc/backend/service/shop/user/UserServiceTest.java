package de.mightypc.backend.service.shop.user;

import de.mightypc.backend.exception.shop.user.UserNotFoundException;
import de.mightypc.backend.model.shop.user.CreateUser;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.model.shop.user.UserResponse;
import de.mightypc.backend.repository.shop.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getLoggedInUser_shouldReturnUserResponse_whenAuthenticated() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);
        User user = new User(UUID.randomUUID().toString(), "user@example.com", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, "CUSTOMER", ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)), "");
        when(userRepository.getUserByEmail("user@example.com")).thenReturn(user);

        // Act
        UserResponse result = userService.getLoggedInUser(authentication);

        // Assert
        assertNotNull(result);
        assertEquals("user@example.com", result.email());
    }

    @Test
    void getLoggedInUser_shouldReturnNull_whenNotAuthenticated() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        UserResponse result = userService.getLoggedInUser(authentication);

        // Assert
        assertNull(result);
    }

    @Test
    void registerUserWithEmailAndPassword_shouldSaveUser() {
        // Arrange
        CreateUser createUser = new CreateUser("user@example.com", "password");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        User savedUser = new User(UUID.randomUUID().toString(), "user@example.com", "encodedPassword", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, "CUSTOMER", ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)), "");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        userService.registerUserWithEmailAndPassword(createUser);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserWithEmailAndPassword_shouldThrowException_whenUserAlreadyExists() {
        // Arrange
        CreateUser createUser = new CreateUser("user@example.com", "password");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.registerUserWithEmailAndPassword(createUser));
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        // Arrange
        User user = new User(UUID.randomUUID().toString(), "user@example.com", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, "CUSTOMER", ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)), "");
        when(userRepository.findById("userId")).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById("userId");

        // Assert
        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void getUserById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById("userId")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("userId"));
    }

    @Test
    void attachPhoto_shouldSaveUserWithPhoto() {
        // Arrange
        User user = new User(UUID.randomUUID().toString(), "user@example.com", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, "CUSTOMER", ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)), "");
        when(userRepository.findById("userId")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.attachPhoto("userId", "photoUrl");

        // Assert
        assertEquals("photoUrl", result.getUserPhoto());
    }

    @Test
    void deleteImage_shouldRemoveUserPhoto() {
        // Arrange
        User user = new User(UUID.randomUUID().toString(), "user@example.com", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, "CUSTOMER", ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)), "photoUrl");
        when(userRepository.findById("userId")).thenReturn(Optional.of(user));

        // Act
        userService.deleteImage("userId");

        // Assert
        assertEquals("", user.getUserPhoto());
        verify(userRepository).save(user);
    }

    @Test
    void deleteAccount_shouldDeleteUser() {
        // Arrange
        User user = new User(UUID.randomUUID().toString(), "user@example.com", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, "CUSTOMER", ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL)), "");
        when(userRepository.findById("userId")).thenReturn(Optional.of(user));

        // Act
        userService.deleteAccount("userId");

        // Assert
        verify(userRepository).delete(user);
    }
}
