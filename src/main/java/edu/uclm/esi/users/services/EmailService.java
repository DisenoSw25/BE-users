package edu.uclm.esi.users.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.uclm.esi.users.model.User;

@Service
public class EmailService {
	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendVerificationEmail(User user, String token) {
		String verificationUrl = "http://localhost:8081/users/verificar?token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Verifica tu cuenta");
		message.setText("\t\t----BIENVENIDO----\n Haz clic en el siguiente enlace para verificar tu cuenta: "
				+ verificationUrl);

		mailSender.send(message);
	}

	public void sendPasswordResetEmail(User user, String token) {
		String resetUrl = "http://localhost:8081/password/reset?token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Restablecimiento de contraseña");
		message.setText("\t\t----RESTABLECER CONTRASEÑA----\n \nTienes 15 minutos para reestablecer tu contraseña"
				+ "Haz clic en el siguiente enlace para restablecerla: " + resetUrl +"\n\nSi no solicitaste este cambio, ignora este mensaje");

		mailSender.send(message);
	}
}
