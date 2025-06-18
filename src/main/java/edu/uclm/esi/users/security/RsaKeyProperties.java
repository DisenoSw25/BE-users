package edu.uclm.esi.users.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) { // NUEVO
    public RsaKeyProperties {
        if (publicKey == null || privateKey == null) {
			throw new IllegalArgumentException("Both public and private keys must be provided.");  
        }
    }

}
