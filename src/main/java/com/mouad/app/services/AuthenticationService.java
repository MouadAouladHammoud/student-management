package com.mouad.app.services;
import com.mouad.app.repositories.StudentRepository;
import com.mouad.app.requests.AuthenticationRequest;
import com.mouad.app.requests.AuthenticationResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final StudentRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Jwt.JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // *** Connexion de l'utilisateur *** //
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()
            )
        );

        var student = repository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(student, student.getId());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
