package com.buddy.studybuddy.controllers;

import com.buddy.studybuddy.dtos.ErrorDTO;
import com.buddy.studybuddy.entities.User;
import com.buddy.studybuddy.dtos.LoginUserDto;
import com.buddy.studybuddy.dtos.RegisterUserDto;
import com.buddy.studybuddy.exceptions.BusinessException;
import com.buddy.studybuddy.repositories.UserRepository;
import com.buddy.studybuddy.responses.LoginResponse;
import com.buddy.studybuddy.services.AuthenticationService;
import com.buddy.studybuddy.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;
    @Autowired
    UserRepository userRepository;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponseDTO("Error: Email is already taken!"));
            ErrorDTO err = new ErrorDTO();
            err.setCode("AUTH_005");
            err.setMessage("Email is already taken!");

            throw new BusinessException(List.of(err));
        }
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            throw new RuntimeException("User is not authenticated. Please log in via Google.");
        }

        // Fetch user from DB
        String email = user.getAttribute("email");
        Optional<User> dbUser = userRepository.findByEmail(email);

        // Merge OAuth2 attributes with DB data
        Map<String, Object> attributes = new HashMap<>(user.getAttributes());
        dbUser.ifPresent(value -> attributes.put("dbId", value.getId()));

        return attributes;
    }


}