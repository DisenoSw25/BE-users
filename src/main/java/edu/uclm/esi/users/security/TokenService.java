package edu.uclm.esi.users.security;

import java.time.Instant;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder; 

    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }
     // generar un token de verificación de email
    public String generateVerificationToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(86400)) // 24 horas
                .subject(authentication.getName())
                .claim("purpose", "emailVerification")
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    //validar el token de verificación de email
    public String validateVerificationToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);

            if (!"emailVerification".equals(jwt.getClaimAsString("purpose"))) {
                throw new IllegalArgumentException("Token no válido para verificación");
            }

            return jwt.getSubject(); // el email del usuario
        } catch (Exception e) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }
    }
    
    // generar un token de acceso normal
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") // Cambia esto por tu URL de emisor
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600)) // Token válido por 1 hora
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("isAuthenticated", authentication.isAuthenticated())
                .build();
                
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Authentication getAuthentication(Jwt jwt) {
        String username = jwt.getSubject();
        String scope = jwt.getClaimAsString("scope");

        var authorities = Arrays.stream(scope.split(" "))
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
    
    // Generar un token de recuperación de contraseña
    public String generatePasswordResetToken(String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(900)) // 15 minutos
                .subject(email)
                .claim("purpose", "passwordReset")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    // Validar el token de recuperación de contraseña
    public String validatePasswordResetToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);

            if (!"passwordReset".equals(jwt.getClaimAsString("purpose"))) {
                throw new IllegalArgumentException("Token no válido para recuperación");
            }

            return jwt.getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }
    }

}
