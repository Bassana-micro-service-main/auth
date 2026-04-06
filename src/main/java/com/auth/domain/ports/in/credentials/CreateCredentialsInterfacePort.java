package com.auth.domain.ports.in.credentials;

import com.auth.domain.entities.CredentialsEntity;
import java.util.UUID;

/**
 * Port entrant (driving) : création d'une entité {@link CredentialsEntity}.
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

	CredentialsEntity create(CreateCredentialsCommand command);
}
