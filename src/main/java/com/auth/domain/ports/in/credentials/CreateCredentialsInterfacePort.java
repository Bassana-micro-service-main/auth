package com.auth.domain.ports.in.credentials;

import com.auth.domain.entities.Credential;
import java.util.UUID;

/**
 * Port entrant (driving) : création d'une entité {@link Credential}.
 * Implémenté par la couche application (cas d'usage), appelé par les adaptateurs (HTTP, messages, etc.).
 */
public interface CreateCredentialsInterfacePort {

	/**
	 * Données nécessaires pour créer des identifiants de connexion (sans identifiants techniques générés côté application).
	 */
	record CreateCredentialsCommand(
			UUID userId,
			String email,
			String hashedPassword,
			String passwordSalt
	) {
	}

	Credential create(CreateCredentialsCommand command);
}
