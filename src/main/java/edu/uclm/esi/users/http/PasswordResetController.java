package edu.uclm.esi.users.http;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.uclm.esi.users.dao.PasswordResetTokenDAO;
import edu.uclm.esi.users.services.EmailService;
import edu.uclm.esi.users.services.UserService;
import edu.uclm.esi.users.security.TokenService;

@RestController
@RequestMapping("/password")
@CrossOrigin(origins = "*")
public class PasswordResetController {

	private final UserService userService;
	private final TokenService tokenService;
	private final EmailService emailService;
	private final PasswordResetTokenDAO tokenDAO;

	public PasswordResetController(UserService userService, TokenService tokenService, EmailService emailService, PasswordResetTokenDAO tokenDAO) {
		this.userService = userService;
		this.tokenService = tokenService;
		this.emailService = emailService;
		this.tokenDAO = tokenDAO;
	}

	// Paso 1: Solicitar recuperación
	@PostMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestParam String email) {
		try {
			userService.iniciarRecuperacionPassword(email, tokenService, emailService, tokenDAO);
			return ResponseEntity.ok("Correo de recuperación enviado");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Paso 2: Validar token (intermedio)
	@GetMapping("/validateToken")
	public ResponseEntity<String> validateToken(@RequestParam String token) {
		try {
			var resetToken = tokenDAO.findById(token)
				.orElseThrow(() -> new RuntimeException("Token no encontrado"));

			if (resetToken.isUsed())
				throw new RuntimeException("Este token ya ha sido usado");

			if (resetToken.getExpiresAt().isBefore(Instant.now()))
				throw new RuntimeException("Token expirado");

			tokenService.validatePasswordResetToken(token); // puede lanzar excepción

			return ResponseEntity.ok("Token válido");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("El token no es válido o ha expirado");
		}
	}

	// Paso 3: Cambiar contraseña
	@PostMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String nuevaPassword) {
		try {
			userService.cambiarPasswordConToken(token, nuevaPassword);
			return ResponseEntity.ok("Contraseña cambiada correctamente");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}


