package com.mouad.app.services;

import com.mouad.app.entities.Student;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.mouad.app.repositories.TokenRepository;

public class Jwt {

    @Service
    public static class JwtService {

        @Value("${application.security.jwt.secret-key}")
        private String secretKey;

        @Value("${application.security.jwt.expiration}")
        private long jwtExpiration;

        @Value("${application.security.jwt.refresh-token.expiration}")
        private long refreshExpiration;

        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }

        public String generateToken(UserDetails userDetails, Student student) {
            return generateToken(new HashMap<>(), userDetails, student);
        }

        public String generateToken(
                Map<String, Object> extraClaims,
                UserDetails userDetails,
                Student student
        ) {
            return buildToken(extraClaims, userDetails, student, jwtExpiration);
        }

        public String generateRefreshToken(UserDetails userDetails, Student student) {
            return buildToken(new HashMap<>(), userDetails, student, refreshExpiration);
        }

        private String buildToken(
                Map<String, Object> extraClaims,
                UserDetails userDetails,
                Student student,
                long expiration
        ) {
            return Jwts
                    .builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .claim("userId", student.getId()) // Ajouter l'ID de l'utilisateur comme une revendication supplémentaire
                    .claim("role", student.getUserRole()) // Ajouter Role de l'utilisateur
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        public boolean isTokenValid(String token, UserDetails userDetails) {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        }

        private boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        private Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        private Claims extractAllClaims(String token) {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }

        private Key getSignInKey() {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        }

    }

    @Component
    @RequiredArgsConstructor
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final UserDetailsService userDetailsService;
        private final TokenRepository tokenRepository;

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain
        ) throws ServletException, IOException {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;

            // Vérifier la présence du header Authorization
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extraction du token JWT sans le préfixe "Bearer "
            jwt = authHeader.substring(7);

            try {
                userEmail = jwtService.extractUsername(jwt); // Extraction de l'email ou du nom d'utilisateur à partir du token

                // Vérifier si le token JWT est valide et l'utilisateur n'est pas déjà authentifié dans le contexte de sécurité
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                    // Vérifier si le token n'est pas expiré ou révoqué dans le repository de tokens
                    var isTokenValid = tokenRepository.findByToken(jwt)
                            .map(token -> !token.isExpired() && !token.isRevoked())
                            .orElse(false);


                    // Valide le token et définit l'authentification dans le contexte de sécurité
                    if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }

            } catch (ExpiredJwtException e) {
                // Gère l'exception pour un token expiré
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has expired");
                return;
            } catch (JwtException | IllegalArgumentException e) {
                // Gère les exceptions pour un token invalide ou autres erreurs JWT
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
            filterChain.doFilter(request, response);
        }
    }
}
