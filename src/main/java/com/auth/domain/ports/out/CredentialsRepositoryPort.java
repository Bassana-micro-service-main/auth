package com.auth.domain.ports.out;

import com.auth.domain.entities.CredentialsEntity;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant (driven) : persistance des {@link CredentialsEntity}.
 * Implémenté dans l'infrastructure (ex. JPA).
 */
public interface CredentialsRepositoryPort {

	String REPOSITORY_QUALIFIER = "credentialsRepository";

	CredentialsEntity save(CredentialsEntity entity);

	Optional<CredentialsEntity> findById(UUID id);

	Optional<CredentialsEntity> findByPublicId(String publicId);

	Optional<CredentialsEntity> findByEmail(String email);

	void delete(String publicId);
}
