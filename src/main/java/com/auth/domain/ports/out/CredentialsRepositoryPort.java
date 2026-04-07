package com.auth.domain.ports.out;

import com.auth.domain.entities.Credential;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant (driven) : persistance des {@link Credential}.
 * Implémenté dans l'infrastructure (ex. JPA).
 */
public interface CredentialsRepositoryPort {

	String REPOSITORY_QUALIFIER = "credentialsRepository";

	Credential save(Credential entity);

	Optional<Credential> findById(UUID id);

	Optional<Credential> findByPublicId(String publicId);

	Optional<Credential> findByEmail(String email);

	void delete(String publicId);
}
