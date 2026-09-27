package com.edu.erpbackend.security;

import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.Teacher;
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
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        User user = userRepository.findByEmail(email).orElseThrow();

        // Resolve profile image (Student vs Teacher)
        String profileImageUrl = null;
        if (user instanceof Student s) {
            profileImageUrl = s.getProfileImageUrl();
        } else if (user instanceof Teacher t) {
            profileImageUrl = t.getProfileImageUrl();
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // Where to send the user after OAuth success
        String frontendUrl = System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:5173");

        // Build the redirect URL with all the info the frontend needs
        StringBuilder q = new StringBuilder();
        q.append("?token=").append(enc(token));
        q.append("&role=").append(enc(user.getRole().name()));
        q.append("&userId=").append(enc(user.getId().toString()));
        q.append("&name=").append(enc(user.getName()));
        q.append("&email=").append(enc(user.getEmail()));
        if (profileImageUrl != null) {
            q.append("&profileImageUrl=").append(enc(profileImageUrl));
        }

        String targetUrl = frontendUrl + "/oauth-callback" + q;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}