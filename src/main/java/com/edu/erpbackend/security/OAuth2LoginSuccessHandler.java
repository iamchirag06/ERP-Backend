package com.edu.erpbackend.security;

import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.users.UserRepository;
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
        User user = userRepository.findByEmail(email).orElseThrow();

        // 2. Generate the JWT Token (✅ FIXED: Now passing Role)
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // 3. Redirect to Frontend with Token
        String targetUrl = "http://localhost:3000/oauth-callback?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}