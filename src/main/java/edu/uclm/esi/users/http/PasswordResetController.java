package edu.uclm.esi.users.http;

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

	@PostMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestParam String email) {
		try {
			userService.iniciarRecuperacionPassword(email, tokenService, emailService, tokenDAO);
			return ResponseEntity.ok("Correo de recuperación enviado");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String nuevaPassword) {
		try {
			userService.cambiarPasswordConToken(token, nuevaPassword); // ya no pasas los DAOs ni el servicio
			return ResponseEntity.ok("Contraseña cambiada correctamente");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
