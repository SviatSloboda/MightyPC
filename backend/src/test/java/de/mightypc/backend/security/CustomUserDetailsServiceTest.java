package de.mightypc.backend.security;

import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists() {
        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(user.getEmail()).thenReturn("user@example.com");
        when(user.getPassword()).thenReturn("password");
        when(user.getRole()).thenReturn("ROLE_USER");

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user@example.com");

        Assertions.assertEquals("user@example.com", userDetails.getUsername());
        Assertions.assertEquals("password", userDetails.getPassword());
        Assertions.assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_UserDoesNotExist() {
        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("user@example.com");
        });
    }
}
