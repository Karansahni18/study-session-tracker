package com.karan.studysessiontracker.service;

import com.karan.studysessiontracker.dto.request.LoginRequest;
import com.karan.studysessiontracker.dto.request.RegisterRequest;
import com.karan.studysessiontracker.dto.response.AuthResponse;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.exception.DuplicateResourceException;
import com.karan.studysessiontracker.repository.UserRepository;
import com.karan.studysessiontracker.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_throwsDuplicate_whenUsernameAlreadyTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("karan");
        request.setPassword("secret123");

        when(userRepository.findByUsername("karan"))
                .thenReturn(Optional.of(new User(1L, "karan", "hash", "USER")));

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_hashesPasswordAndSavesUserWithUserRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("karan");
        request.setPassword("secret123");

        when(userRepository.findByUsername("karan")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(jwtUtil.generateToken("karan")).thenReturn("token-123");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("karan", saved.getUsername());
        assertEquals("HASHED", saved.getPassword());
        assertNotEquals("secret123", saved.getPassword());
        assertEquals("USER", saved.getRole());
        assertEquals("token-123", response.getToken());
        assertEquals("karan", response.getUsername());
    }

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("karan");
        request.setPassword("secret123");

        when(jwtUtil.generateToken("karan")).thenReturn("token-123");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("token-123", response.getToken());
    }

    @Test
    void login_neverGeneratesToken_whenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("karan");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(jwtUtil, never()).generateToken(any());
    }

}