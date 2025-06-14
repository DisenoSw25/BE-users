package edu.uclm.esi.users.model;


import jakarta.persistence.*;

import java.util.UUID;
import java.util.regex.Pattern;

@Entity

@Table(name = "Users")
public class User {
	@Id
	@GeneratedValue(generator = "UUID")
	@Column(name = "user_id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "surname", nullable = false)
	private String surname;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;
    
	@Column(name = "is_verified")
	private boolean isVerified;
	
	@Column(name = "credito")
	private double credito;
	
	public User () {
		// Constructor vacío para JPA
	}
	
	public User(UUID id, String name, String surname, String email, String password, boolean isVerified,
			double credito) {
		super();
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.isVerified = isVerified;
		this.credito = credito;
	}

	public boolean comprobarFormatoPassword() {
		boolean valida = true;

		int longitudMinima = 7;
		String mayus = ".*[A-Z].*";
		String minus = ".*[a-z].*";
		String digit = ".*\\d*.";
		String specialCharacters = ".*[!@#\\$%\\^&\\*].*";

		// Verificar longitud mínima
		if (password.length() < longitudMinima) {
			valida = false;
		}

		// Verificar que tenga al menos una letra mayúscula
		if (!Pattern.matches(mayus, password)) {
			valida = false;
		}

		// Verificar que tenga al menos una letra minúscula
		if (!Pattern.matches(minus, password)) {
			valida = false;
		}

		// Verificar que tenga al menos un dígito
		if (!Pattern.matches(digit, password)) {
			valida = false;
		}

		// Verificar que tenga al menos un carácter especial
		if (!Pattern.matches(specialCharacters, password)) {
			valida = false;
		}

		// Si cumple con todas las condiciones
		return valida;
	}

	public boolean comprobarFormatoEmail() {
		boolean valido = true;
		String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		if (!Pattern.matches(emailPattern, email)) {
			valido = false;
		}
		return valido;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public double getCredito() {
		return credito;
	}

	public void setCredito(double credito) {
		this.credito = credito;
	}
	
	
}
