package edu.uclm.esi.users.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;

    public JwtCookieAuthenticationFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                                    throws ServletException, IOException {

        String token = extractTokenFromCookies(request);
        if (token != null) {
            try {
                Jwt jwt = jwtDecoder.decode(token);
                String username = jwt.getSubject();
                String scope = jwt.getClaimAsString("scope");

                var authorities = Arrays.stream(scope.split(" "))
                                .map(SimpleGrantedAuthority::new)
                                .toList();

                Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e) {
                System.out.println("Invalid JWT: " + e.getMessage());
                // Deja sin autenticación pero no lanza excepción
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
