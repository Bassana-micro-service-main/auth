package com.auth.domain.ports.in.auth;

/**
 * Déconnexion : révoque la session associée au refresh token.
 */
public interface LogoutInterfacePort {

	void logout(LogoutCommand command);

	record LogoutCommand(String refreshToken) {
	}
}
