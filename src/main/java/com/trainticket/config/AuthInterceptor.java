package com.trainticket.config;

import com.trainticket.model.Role;
import com.trainticket.model.User;
import com.trainticket.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // Get userId from header
        String userIdHeader = request.getHeader("X-User-Id");

        if (userIdHeader == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Missing X-User-Id header\"}");
            return false;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid user ID\"}");
            return false;
        }

        // Check if user exists and is ADMIN
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"User not found\"}");
            return false;
        }

        if (user.getRole() != Role.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"Admin access required\"}");
            return false;
        }

        // Store user in request for later use
        request.setAttribute("currentUser", user);
        return true;
    }
}