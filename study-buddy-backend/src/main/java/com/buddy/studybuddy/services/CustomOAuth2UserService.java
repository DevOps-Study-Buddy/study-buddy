package com.buddy.studybuddy.services;

import com.buddy.studybuddy.entities.RoleEnum;
import com.buddy.studybuddy.entities.User;
import com.buddy.studybuddy.entities.Role;
import com.buddy.studybuddy.repositories.UserRepository;
import com.buddy.studybuddy.repositories.RoleRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String randomPassword = UUID.randomUUID().toString();
    String encodedPassword = passwordEncoder.encode(randomPassword);

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");
        String oauthId = oAuth2User.getAttribute("sub");



        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            Role defaultRole = roleRepository.findByName(RoleEnum.USER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));

            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPassword(encodedPassword);
            newUser.setOauthProvider("GOOGLE");
            newUser.setOauthId(oauthId);
            newUser.setRole(defaultRole);

            userRepository.save(newUser);
        }

        return oAuth2User;
    }
}
