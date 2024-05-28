package de.mightypc.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.environment}")
    private String environment;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/configurator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/configurator/**").authenticated()
                        .requestMatchers("/api/user-pcs/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/hardware/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pc/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/configuration/calculate-energy-consumption").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/workstation/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/{userId}/change-password").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/{userId}/set-password").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/user/{userId}/set-password").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/basket/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/basket/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/basket/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/order/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/order/**").authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .httpBasic(withDefaults())
                .oauth2Login(oauth -> {
                    oauth.loginPage("/oauth2/authorization/google");
                    if ("prod".equals(environment)) {
                        oauth.defaultSuccessUrl("/", true);
                    } else {
                        oauth.defaultSuccessUrl("http://localhost:5173", true);
                    }
                })
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll());
        return http.build();
    }
}
