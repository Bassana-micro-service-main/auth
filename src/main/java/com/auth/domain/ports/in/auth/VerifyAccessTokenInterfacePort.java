package com.auth.domain.ports.in.auth;

/**
 * Vérifie un bearer access token pour les appels gateway -> service.
 */
public interface VerifyAccessTokenInterfacePort {

	void verify(VerifyAccessTokenCommand command);

	record VerifyAccessTokenCommand(String authorizationHeader) {
	}
}
