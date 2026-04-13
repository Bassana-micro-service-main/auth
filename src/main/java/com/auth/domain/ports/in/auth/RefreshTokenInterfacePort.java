package com.auth.domain.ports.in.auth;

/**
 * Rafraîchit le jeton d’accès à partir d’un refresh token valide.
 */
public interface RefreshTokenInterfacePort {

	AuthSessionResult refresh(RefreshTokenCommand command);

	record RefreshTokenCommand(String refreshToken) {
	}
}
