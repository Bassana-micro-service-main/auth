package com.auth.domain.ports.out;

import com.auth.domain.entities.Credential;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant (driven) : persistance des {@link Credential}.
 * Implémentation typique : {@link com.auth.adapter.out.persistence.CredentialsRepositoryAdapter} (JPA / Hibernate).
 */
public interface CredentialsRepositoryPort {

	String REPOSITORY_QUALIFIER = "credentialsRepository";

	Credential save(Credential entity);

	Optional<Credential> findById(UUID id);

	Optional<Credential> findByPublicId(String publicId);

	Optional<Credential> findByEmail(String email);

	Optional<Credential> findByUserId(UUID userId);

	void delete(String publicId);
}
