package com.auth.domain.ports.in.credentials;

/**
 * Port entrant (driving) : suppression des identifiants de connexion.
 */
public interface DeleteCredentialsInterfacePort {

	record DeleteCredentialsCommand(String publicId) {
	}

	void delete(DeleteCredentialsCommand command);
}
