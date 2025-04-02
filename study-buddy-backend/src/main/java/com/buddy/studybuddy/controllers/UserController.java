package com.buddy.studybuddy.controllers;

import com.buddy.studybuddy.entities.User;
import com.buddy.studybuddy.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/me")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<User> authenticatedUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        User currentUser = (User) authentication.getPrincipal();
//
//        return ResponseEntity.ok(currentUser);
//    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            // If the user is authenticated via JWT and is a custom User entity
            return ResponseEntity.ok((User) principal);
        } else if (principal instanceof DefaultOAuth2User) {
            // If the user is authenticated via OAuth2
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) principal;

            // Extract useful information
            String email = oAuth2User.getAttribute("email"); // Adjust based on your OAuth2 provider
            String name = oAuth2User.getAttribute("name");

            // Create a response with the necessary details
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("name", name);
            response.put("roles", authentication.getAuthorities());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }


    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
}