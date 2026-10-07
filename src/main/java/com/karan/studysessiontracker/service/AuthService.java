package com.karan.studysessiontracker.service;

import com.karan.studysessiontracker.dto.request.LoginRequest;
import com.karan.studysessiontracker.dto.request.RegisterRequest;
import com.karan.studysessiontracker.dto.response.AuthResponse;
import com.karan.studysessiontracker.entity.User;
import com.karan.studysessiontracker.repository.UserRepository;
import com.karan.studysessiontracker.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.karan.studysessiontracker.exception.DuplicateResourceException;


@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager, JwtUtil jwtUtil) {

                            this.userRepository = userRepository;
                            this.passwordEncoder = passwordEncoder;
                            this.authenticationManager = authenticationManager;
                            this.jwtUtil = jwtUtil;
                        }

    public AuthResponse register(RegisterRequest request) {
        
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username is already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername());

        }
    
        public AuthResponse login(LoginRequest request) {
            
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername()
                , request.getPassword()
            ));

            String token = jwtUtil.generateToken(request.getUsername());
            return new AuthResponse(token, request.getUsername());
        }
}
