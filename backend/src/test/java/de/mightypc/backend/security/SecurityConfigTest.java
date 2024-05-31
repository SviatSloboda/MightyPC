package de.mightypc.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@EnableWebSecurity
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private HttpSecurity httpSecurity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockHttpSecurity();
    }

    private void mockHttpSecurity() {
        try {
            when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
            when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
            when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
            when(httpSecurity.addFilterBefore(any(UsernamePasswordAuthenticationFilter.class), eq(UsernamePasswordAuthenticationFilter.class))).thenReturn(httpSecurity);
            when(httpSecurity.logout(any())).thenReturn(httpSecurity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void passwordEncoder() {
        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertNotNull(passwordEncoder);
    }

    @Test
    void authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = securityConfig.authenticationProvider(customUserDetailsService);
        assertNotNull(authenticationProvider);
    }

    @Test
    void authenticationManager() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mock(AuthenticationManager.class));
        AuthenticationManager authenticationManager = securityConfig.authenticationManager(authenticationConfiguration);
        assertNotNull(authenticationManager);
    }
}
