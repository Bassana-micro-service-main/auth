package com.auth.domain.ports.in.credentials;

import com.auth.domain.entities.CredentialsEntity;
import java.util.Optional;
import java.util.UUID;

/**
 * Port entrant (driving) : consultation d'une entité {@link CredentialsEntity}.
 * Les critères passent uniquement par des requêtes ({@code Query}), pas par des paramètres libres.
 */
public interface GetCredentialsInterfacePort {

	record FindByPublicIdQuery(String publicId) {
	}

	record FindByEmailQuery(String email) {
	}

	record FindByUserIdQuery(UUID userId) {
	}

	Optional<CredentialsEntity> findByPublicId(FindByPublicIdQuery query);

	Optional<CredentialsEntity> findByEmail(FindByEmailQuery query);

	Optional<CredentialsEntity> findByUserId(FindByUserIdQuery query);
}
