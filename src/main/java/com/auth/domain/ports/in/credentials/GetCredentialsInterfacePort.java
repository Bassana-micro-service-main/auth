package com.auth.domain.ports.in.credentials;

import com.auth.domain.entities.Credential;
import java.util.Optional;
import java.util.UUID;

/**
 * Port entrant (driving) : consultation d'une entité {@link Credential}.
 * Les critères passent uniquement par des requêtes ({@code Query}), pas par des paramètres libres.
 */
public interface GetCredentialsInterfacePort {

	record FindByPublicIdQuery(String publicId) {
	}

	record FindByEmailQuery(String email) {
	}

	record FindByUserIdQuery(UUID userId) {
	}

	Optional<Credential> findByPublicId(FindByPublicIdQuery query);

	Optional<Credential> findByEmail(FindByEmailQuery query);

	Optional<Credential> findByUserId(FindByUserIdQuery query);
}
