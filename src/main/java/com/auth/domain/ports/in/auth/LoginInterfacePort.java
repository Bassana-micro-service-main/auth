package com.auth.domain.ports.in.auth;

/**
 * Connexion : vérifie les identifiants et émet session + jetons.
 */
public interface LoginInterfacePort {

	AuthSessionResult login(LoginCommand command);

	record LoginCommand(
			String email,
			String plainPassword,
			String ipAddress,
			String userAgent,
			String deviceName
	) {
	}
}
