package com.auth.adapter.out.security;

import com.auth.domain.ports.out.PasswordCryptoPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Bcrypt via Spring Security (port sortant {@link PasswordCryptoPort}).
 */
@Component
@Qualifier(PasswordCryptoPort.QUALIFIER)
public class PasswordCryptoAdapter implements PasswordCryptoPort {

	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Override
	public String hash(String rawPassword) {
		return encoder.encode(rawPassword);
	}

	@Override
	public boolean matches(String rawPassword, String storedHash) {
		return encoder.matches(rawPassword, storedHash);
	}
}
