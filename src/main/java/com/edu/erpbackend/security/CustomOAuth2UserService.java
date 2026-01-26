package com.edu.erpbackend.security;

import com.edu.erpbackend.model.User;
import com.edu.erpbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Fetch user details from Google
        OAuth2User oauth2User = super.loadUser(userRequest);
        String email = oauth2User.getAttribute("email");

        // 2. 🛡️ DOMAIN CHECK (The Strict Rule)
        if (email == null || !email.endsWith("@satyug.edu.in")) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_domain"),
                    "Access Denied: Only @satyug.edu.in emails are allowed.");
        }

        // 3. (Optional) Check if they are already registered in your DB
        // If you want to auto-register them, remove this block and add save logic instead.
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("user_not_found"),
                    "You are not registered in the ERP system. Please contact the Admin.");
        }

        return oauth2User;
    }
}