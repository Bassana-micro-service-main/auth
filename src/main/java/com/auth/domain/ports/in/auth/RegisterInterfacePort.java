package com.auth.domain.ports.in.auth;

/**
 * Inscription : crée l’utilisateur dans user_profile puis les credentials locaux, puis émet session + jetons.
 */
public interface RegisterInterfacePort {

	AuthSessionResult register(RegisterCommand command);

	record RegisterCommand(
			String email,
			String plainPassword,
			String ipAddress,
			String userAgent,
			String deviceName
	) {
	}
}
