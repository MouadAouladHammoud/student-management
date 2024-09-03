package com.mouad.app.services;
import com.mouad.app.entities.Student;
import com.mouad.app.repositories.StudentRepository;
import com.mouad.app.requests.AuthenticationRequest;
import com.mouad.app.requests.AuthenticationResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mouad.app.entities.Token;
import com.mouad.app.repositories.TokenRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final StudentRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Jwt.JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

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

        this.revokeAllUserTokens(student);
        this.saveTokenForUser(student, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    private void saveTokenForUser(Student student, String jwtToken) {
        var token = Token.builder()
                .student(student)
                .token(jwtToken)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Student student) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(student.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
