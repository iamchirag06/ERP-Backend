package com.edu.erpbackend.security;

import com.edu.erpbackend.model.User;
import com.edu.erpbackend.repository.UserRepository;
import com.edu.erpbackend.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        // 1. Get the User entity
        // We can safely .get() because CustomOAuth2UserService already checked existence
        User user = userRepository.findByEmail(email).orElseThrow();

        // 2. Generate the JWT Token
        String token = jwtUtil.generateToken(user.getEmail());

        // 3. Redirect to Frontend with Token
        // ⚠️ CHANGE THIS URL if your React/Frontend runs on a different port (e.g., 3000 or 5173)
        String targetUrl = "http://localhost:3000/oauth-callback?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}