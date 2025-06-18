package edu.uclm.esi.users.http;

import edu.uclm.esi.users.model.User;
import edu.uclm.esi.users.services.EmailService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    private final EmailService emailService;

    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test-send-email")
    public String testSendEmail(@RequestParam String toEmail) {
        User user = new User();
        user.setEmail(toEmail);
        String dummyToken = "token-prueba-123";

        try {
            emailService.sendVerificationEmail(user, dummyToken);
            return "Correo enviado a " + toEmail;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error enviando correo: " + e.getMessage();
        }
    }
}
