package edu.uclm.esi.users.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class PasswordResetToken {
    @Id
    private String token;

    @OneToOne
    private User user;

    private Instant expiresAt;

    private boolean used;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, User user, Instant expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.used = false;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}


    
}
