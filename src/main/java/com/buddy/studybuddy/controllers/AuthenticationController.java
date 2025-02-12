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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            err .setCode("AUTH_005");
            err.setMessage("Email is already taken!");
            System.out.println("ErrorDTO: " + err);
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
}