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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/configurator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/configurator/**").authenticated()
                        .requestMatchers("/api/user-pcs/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user-pcs/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/hardware/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pc/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/configuration/calculate-energy-consumption").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/workstation/**").permitAll()
                        .requestMatchers("/api/user/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/login").permitAll()
                        .requestMatchers("/api/user/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/basket/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/basket/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/order/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/order/**").authenticated()
                        .anyRequest().authenticated())
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
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
