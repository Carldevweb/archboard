package com.Carldevweb.archboard.user.app;

import com.Carldevweb.archboard.security.JwtService;
import com.Carldevweb.archboard.user.api.dto.AuthResponse;
import com.Carldevweb.archboard.user.api.dto.LoginRequest;
import com.Carldevweb.archboard.user.api.dto.RegisterRequest;
import com.Carldevweb.archboard.user.domain.Role;
import com.Carldevweb.archboard.user.domain.User;
import com.Carldevweb.archboard.user.infra.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuthenticationManager authManager;

    public AuthService(
            UserRepository users,
            PasswordEncoder encoder,
            JwtService jwt,
            AuthenticationManager authManager
    ) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.authManager = authManager;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();

        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already used");
        }

        User user = new User(email, encoder.encode(req.password()), Role.USER);
        User saved = users.save(user);

        String token = jwt.generateToken(
                saved.getId(),
                saved.getEmail(),
                saved.getRole().name()
        );

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, req.password())
        );

        User user = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwt.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(token);
    }
}