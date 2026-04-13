package com.auth.domain.ports.out;

/**
 * Hachage et vérification des mots de passe (ex. bcrypt). Implémenté hors domaine.
 */
public interface PasswordCryptoPort {

	String QUALIFIER = "passwordCrypto";

	String hash(String rawPassword);

	boolean matches(String rawPassword, String storedHash);
}
