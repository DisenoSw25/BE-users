package edu.uclm.esi.users.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import edu.uclm.esi.users.security.RsaKeyProperties;
import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Usar BCrypt para codificar contraseñas
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtCookieAuthenticationFilter jwtFilter)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF para simplificar (no recomendado para
                                                       // producción)
                .cors(Customizer.withDefaults()) // Habilitar CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login", "/users/registro", "/test-send-email", 
                        		"password/forgotPassword", "password/resetPassword", "users/checkVerificado/**").permitAll() // Permitir acceso a todos los usuarios
                        .anyRequest().authenticated()) // Requerir autenticación para cualquier otra solicitud
                .oauth2ResourceServer(oauth2 -> oauth2 // NUEVO
                        .jwt(Customizer.withDefaults())) // Configurar JWT para el servidor de recursos
                .sessionManagement(session -> session // NUEVO
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Usar sesiones sin estado (JWT)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // PRUEBA
                .logout(logout -> logout
                        .logoutUrl("/logout") // Escuchamos /logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Borramos la cookie jwt
                            ResponseCookie cookie = ResponseCookie.from("jwt", "")
                                    .httpOnly(true)
                                    .secure(false) // true en producción 
                                    .path("/")
                                    .maxAge(0)
                                    .build();
                            response.setHeader("Set-Cookie", cookie.toString());

                            response.setStatus(HttpServletResponse.SC_OK); // No redirigir, simplemente 200 OK
                        }))
                .build();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Configurar el servicio de detalles de usuario
        authProvider.setPasswordEncoder(passwordEncoder()); // Configurar el codificador de contraseñas
        return authProvider;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Inyectar las propiedades de la clave RSA
    private final RsaKeyProperties properties; 

    public SecurityConfig(RsaKeyProperties properties) {
        this.properties = properties;
    }

    @Bean // PRUEBA
    public JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter(JwtDecoder jwtDecoder) {
        return new JwtCookieAuthenticationFilter(jwtDecoder);
    }

    //decodificar y codificar JWT usando las claves RSA
    @Bean
    JwtDecoder jwtDecoder() { 
        return NimbusJwtDecoder.withPublicKey(properties.publicKey()).build(); // Usar la clave pública para decodificar
                                                                               // JWT
    }

    @Bean
    JwtEncoder jwtEncoder() { // NUEVO
        JWK jwk = new RSAKey.Builder(properties.publicKey())
                .privateKey(properties.privateKey())
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource); // Usar la clave privada para codificar JWT
    }
}
