package com.auth.domain.ports.in.tokens;

/**
 * Port entrant (driving) : suppression d'un jeton.
 */
public interface DeleteTokensInterfacePort {

	record DeleteTokensCommand(String publicId) {
	}

	void delete(DeleteTokensCommand command);
}
