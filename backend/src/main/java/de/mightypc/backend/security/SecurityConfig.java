package de.mightypc.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://mightypc.onrender.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/configurator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/configurator/**").authenticated()
                        .requestMatchers("/api/user-pcs/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user-pcs/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/hardware/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/hardware/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/hardware/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/hardware/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pc/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pc/configuration/calculate-energy-consumption").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/pc/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pc/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pc/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/workstation/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/workstation/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/workstation/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/workstation/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/login").permitAll()
                        .requestMatchers( "/api/basket/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/order/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/order/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/order/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/order/**").hasRole("ADMIN")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter(tokenProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll());
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173", "https://mightypc.onrender.com")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
