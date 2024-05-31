package de.mightypc.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Date;

import static org.mockito.Mockito.when;

@SpringJUnitConfig
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private final String secret = "bSwoFZzvZKWh7xXDmeu0KFlHbpGMrKMlJca41R9VcYtRuiTEW5UBT3e3mQkQHaJMC7cmP8Q27RWHQDOeKjllCw==";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenProvider = new JwtTokenProvider();
        setField(jwtTokenProvider, "jwtSecret", secret);
        setField(jwtTokenProvider, "jwtExpiration", 3000);
    }

    @Test
    void testGenerateToken() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user");

        String token = jwtTokenProvider.generateToken(authentication);

        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        Assertions.assertEquals("user", claims.getSubject());
    }

    @Test
    void testGetUserIdFromJWT() {
        String token = Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        String userId = jwtTokenProvider.getUserIdFromJWT(token);
        Assertions.assertEquals("user", userId);
    }

    @Test
    void testValidateToken_Valid() {
        String token = Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(token);
        Assertions.assertTrue(isValid);
    }

    @Test
    void testValidateToken_Invalid() {
        String token = "invalidToken";

        boolean isValid = jwtTokenProvider.validateToken(token);
        Assertions.assertFalse(isValid);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
