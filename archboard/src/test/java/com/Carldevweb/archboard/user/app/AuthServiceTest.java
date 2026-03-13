package com.Carldevweb.archboard.user.app;

import com.Carldevweb.archboard.security.JwtService;
import com.Carldevweb.archboard.user.api.dto.AuthResponse;
import com.Carldevweb.archboard.user.api.dto.LoginRequest;
import com.Carldevweb.archboard.user.api.dto.RegisterRequest;
import com.Carldevweb.archboard.user.domain.Role;
import com.Carldevweb.archboard.user.domain.User;
import com.Carldevweb.archboard.user.infra.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository users;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwt;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void should_register_user() {
        RegisterRequest request = new RegisterRequest("  User@Test.com  ", "password");
        User savedUser = mock(User.class);

        when(users.existsByEmail("user@test.com")).thenReturn(false);
        when(encoder.encode("password")).thenReturn("encoded-password");
        when(users.save(any(User.class))).thenReturn(savedUser);

        when(savedUser.getId()).thenReturn(1L);
        when(savedUser.getEmail()).thenReturn("user@test.com");
        when(savedUser.getRole()).thenReturn(Role.USER);

        when(jwt.generateToken(1L, "user@test.com", "USER")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertEquals("jwt-token", response.accessToken());

        verify(users).existsByEmail("user@test.com");
        verify(encoder).encode("password");
        verify(users).save(any(User.class));
        verify(jwt).generateToken(1L, "user@test.com", "USER");
    }

    @Test
    void should_throw_when_register_email_already_used() {
        RegisterRequest request = new RegisterRequest("user@test.com", "password");

        when(users.existsByEmail("user@test.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals("Email already used", exception.getMessage());

        verify(users).existsByEmail("user@test.com");
        verify(users, never()).save(any(User.class));
        verify(jwt, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    void should_login_user() {
        LoginRequest request = new LoginRequest("  User@Test.com  ", "password");
        User user = mock(User.class);

        when(users.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("user@test.com");
        when(user.getRole()).thenReturn(Role.USER);
        when(jwt.generateToken(1L, "user@test.com", "USER")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.accessToken());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        verify(authManager).authenticate(captor.capture());

        UsernamePasswordAuthenticationToken authToken = captor.getValue();
        assertEquals("user@test.com", authToken.getPrincipal());
        assertEquals("password", authToken.getCredentials());

        verify(users).findByEmail("user@test.com");
        verify(jwt).generateToken(1L, "user@test.com", "USER");
    }

    @Test
    void should_throw_when_login_user_not_found_after_authentication() {
        LoginRequest request = new LoginRequest("user@test.com", "password");

        when(users.findByEmail("user@test.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals("User not found", exception.getMessage());

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(users).findByEmail("user@test.com");
        verify(jwt, never()).generateToken(anyLong(), anyString(), anyString());
    }
}