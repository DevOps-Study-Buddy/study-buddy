package com.buddy.studybuddy.configs;

import com.buddy.studybuddy.services.JwtService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.buddy.studybuddy.services.CustomOAuth2UserService;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtService jwtService;
    @Value("${frontend.dashboard}")
    private String FrontEndDashboardURL;


    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            CustomOAuth2UserService customOAuth2UserService, JwtService jwtService
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF if using REST API
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated()
                )
//                .oauth2Login(oauth2 -> oauth2
//                        .defaultSuccessUrl("/auth/info", true) // Redirects to /auth/info after login
//                        .permitAll()
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(customOAuth2UserService) // Use custom OAuth2 user service
//                        )
//                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                            OAuth2User oAuth2User = oauthToken.getPrincipal();

                            String token = jwtService.generateOauthToken(oAuth2User);// Generate JWT
                            System.out.println("Auth Token: " + token);
//                            response.sendRedirect("http://localhost:5173/demo?token=" + token);
                            // Store JWT in HTTP-only cookie
                            Cookie jwtCookie = new Cookie("Authorization",token);
                            jwtCookie.setHttpOnly(true);
                            jwtCookie.setSecure(false); // Change to true in production (HTTPS required)
                            jwtCookie.setPath("/");
                            jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

                            response.addCookie(jwtCookie);
                            // Redirect to frontend
                            response.sendRedirect("http://localhost:5173/dashboard");
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
