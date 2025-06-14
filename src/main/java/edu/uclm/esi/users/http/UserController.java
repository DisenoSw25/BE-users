package edu.uclm.esi.users.http;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.users.model.User;
import edu.uclm.esi.users.security.TokenService;
import edu.uclm.esi.users.security.UserPrincipal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import edu.uclm.esi.users.services.EmailService;
import edu.uclm.esi.users.services.UserService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*")
public class UserController {

	private final UserService userService;
	private final TokenService tokenService; 
	private final  EmailService emailService; 
	
	public UserController(UserService userService, TokenService tokenService, EmailService emailService) {
		this.userService = userService;
		this.tokenService = tokenService;
		this.emailService = emailService;
	}
	
	
	private void enviarCorreoVerificacion(User user) {
		Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of());
		String token = tokenService.generateVerificationToken(auth); // versión especial del token
		emailService.sendVerificationEmail(user, token);
	}
	
	@GetMapping("/verificar")
	public ResponseEntity<String> verificarCuenta(@RequestParam String token) {
		try {
			String email = tokenService.validateVerificationToken(token);
			userService.verificarEmail(email);

			return ResponseEntity.ok("Cuenta verificada correctamente.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/registro")
	public ResponseEntity<String> registrar(@RequestBody User user) {
		try {
			userService.registerUser(user);
			enviarCorreoVerificacion(user); //correo verificación 
			return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody User user, HttpSession session) {
        try {
               User loggedInUser = userService.login(user, session);
               UserPrincipal userPrincipal = new UserPrincipal(loggedInUser);
               Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
               String jwtToken = tokenService.generateToken(authentication);

               // Crear cookie httpOnly
               ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
               .httpOnly(true)
               .secure(false) // Usa true en producción con HTTPS
               .path("/")
               .maxAge(3600)
               .sameSite("Lax") // O Strict para más seguridad
               .build();

               return ResponseEntity.ok()
                       .header("Set-Cookie", cookie.toString())
                       .body("Usuario logueado correctamente");
            } catch (ResponseStatusException e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        	
          
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        
	}		

	@GetMapping("/credito")
	public ResponseEntity<Double> obtenerCredito(@RequestParam String email) {
		try {
			double credito = userService.obtenerCreditoPorEmail(email);
			return ResponseEntity.ok(credito);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	
}
