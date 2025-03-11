package edu.uclm.esi.users.http;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("tokens")
@CrossOrigin(origins = "*")
public class TokensController {

	@GetMapping("/checkToken")
	public void checkToken(@RequestParam String token) {
		if (new Random().nextBoolean())
			throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "El servicio solicitado requiere pago");

	}
}
