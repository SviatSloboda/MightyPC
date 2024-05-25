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
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/configurator/**").hasAnyRole("CUSTOMER", "ADMIN", "MANAGER")
                        .requestMatchers("/api/user-pcs/**").hasAnyRole("CUSTOMER", "ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/user-pcs/{userId}/{pcId}/promote").hasAnyRole("MANAGER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/hardware/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/hardware/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/hardware/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/hardware/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/pc/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/pc/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/pc/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "api/configuration/calculate-energy-consumption").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/pc/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/workstation/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/workstation/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/workstation/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/workstation/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/user/{userId}/change-password").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/{userId}/set-password").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/user/{userId}/set-password").authenticated()

                        .requestMatchers(HttpMethod.GET,"/api/basket/**").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/basket/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/basket/**").authenticated()

                        .requestMatchers(HttpMethod.GET,"/api/order/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/order/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/order/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/order/**").hasAnyRole("MANAGER","ADMIN")

                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .httpBasic(withDefaults())
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/google")
                        .defaultSuccessUrl(environment.equals("prod") ? "/" : "http://localhost:5173", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(new OidcUserService())
                                .userService(customOAuth2UserService)
                        )
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/github")
                        .defaultSuccessUrl(environment.equals("prod") ? "/" : "http://localhost:5173", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(new OidcUserService())
                                .userService(customOAuth2UserService)
                        )
                )
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
