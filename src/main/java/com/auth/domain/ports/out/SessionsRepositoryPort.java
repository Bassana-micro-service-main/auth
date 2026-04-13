package com.auth.domain.ports.out;

import com.auth.domain.entities.Session;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant (driven) : persistance des {@link Session}.
 * Implémentation typique : {@link com.auth.adapter.out.persistence.SessionsRepositoryAdapter}.
 */
public interface SessionsRepositoryPort {

	String REPOSITORY_QUALIFIER = "sessionsRepository";

	Session save(Session entity);

	Optional<Session> findById(UUID id);

	Optional<Session> findByPublicId(String publicId);

	List<Session> findByUserId(UUID userId);

	Optional<Session> findByRefreshToken(String refreshToken);

	void delete(String publicId);
}
