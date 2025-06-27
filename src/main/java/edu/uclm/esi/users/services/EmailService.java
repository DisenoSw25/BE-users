package edu.uclm.esi.users.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import edu.uclm.esi.users.model.User;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendVerificationEmail(User user, String token) {
		String verificationUrl = "http://localhost:8081/users/verificar?token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("rosamariafbc@gmail.com");
		message.setTo(user.getEmail());
		message.setSubject("Verifica tu cuenta");
		message.setText("\t\t----BIENVENIDO----\n Haz clic en el siguiente enlace para verificar tu cuenta: "
				+ verificationUrl);

		mailSender.send(message);
		
//		try {
//	        MimeMessage message = mailSender.createMimeMessage();
//	        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
//
//	        helper.setFrom("rosamariafbc@gmail.com");
//	        helper.setTo(toEmail);
//	        helper.setSubject(subject);
//	        helper.setText(body, true); // true permite HTML
//
//	        mailSender.send(message);
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        throw new RuntimeException("Error al enviar correo de verificación", e);
//	    }
	}

	public void sendPasswordResetEmail(User user, String token) {
		String resetUrl = "http://localhost:8081/password/reset?token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setFrom("rosamariafbc@gmail.com");
		message.setSubject("Restablecimiento de contraseña");
		message.setText("\t\t----RESTABLECER CONTRASEÑA----\n \nTienes 15 minutos para reestablecer tu contraseña"
				+ "Haz clic en el siguiente enlace para restablecerla: " + resetUrl +"\n\nSi no solicitaste este cambio, ignora este mensaje");

		mailSender.send(message);
	}
}
