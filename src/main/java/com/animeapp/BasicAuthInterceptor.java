package com.animeapp;

import com.animeapp.model.User;
import com.animeapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BasicAuthInterceptor implements HandlerInterceptor {
    // Fields
    private final UserRepository repo;

    // Constructor
    public BasicAuthInterceptor(UserRepository repo) {
        this.repo = repo;
    }

    // Methods
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Allow OPTIONS (CORS preflight) requests without authentication
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // Allow login, signup (root path), and anime/all endpoints without
        // authentication
        if (path.contains("/login") || path.contains("/anime/all") || path.equals("/")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        // is the header there, is it the right kind?
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // decode the header to a base 64 string
            String b64c = authHeader.substring(6);
            byte[] decoded = Base64.getDecoder().decode(b64c);
            String creds = new String(decoded, StandardCharsets.UTF_8);

            // split the "username:password"
            String[] parts = creds.split(":", 2);
            if (parts.length == 2) {
                String username = parts[0];
                String password = parts[1];

                // check if the user is in the db with matching credentials
                User user = repo.findByUsernameAndPassword(username, password);

                // check if the password is correct
                if (user != null) {
                    return true;
                }
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: invalid credentials");
        return false;
    }
}
