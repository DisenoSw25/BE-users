package edu.uclm.esi.users.http;

import java.util.Map;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*")
public class UserController {

	//Multiples formas de Macario por probar
	@GetMapping("/loginConGetYParametros")//http://localhost:8081/users/loginConGetYParametros?name=pepe&pwd=1234
        public String loginGET(@RequestParam String name, @RequestParam String pwd) {
		return "1234";
	}
	
	@GetMapping("/loginConPathYParametros/{name}")//http://localhost:8081/users/loginConPathYParametros/pepe?pwd=1234
	public String loginPath(@PathVariable String name, @PathVariable String pwd) {
		return "1234";
	}
	
	@PostMapping("/loginConPathYBody")//http://localhost:8081/users/loginConPathYBody/pepe{"pwd":"1234"}
	public String loginPathBody(@PathVariable String name, @RequestBody String pwd) {
		return "1234";
	}
	
	@PostMapping("/loginConBody") //http://localhost:8081/users/loginConBody/{"name":"pepe","pwd":"1234"}
	public String loginBody(@RequestBody Map<String, String> body) {
		return "1234";
	}
	
	
	
}
