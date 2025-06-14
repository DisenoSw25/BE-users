package edu.uclm.esi.users;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import edu.uclm.esi.users.security.RsaKeyProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class UsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

}
