package com.auth.domain.ports.out;

import com.auth.domain.entities.SessionsEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant (driven) : persistance des {@link SessionsEntity}.
 */
public interface SessionsRepositoryPort {

	String REPOSITORY_QUALIFIER = "sessionsRepository";

	SessionsEntity save(SessionsEntity entity);

	Optional<SessionsEntity> findById(UUID id);

	Optional<SessionsEntity> findByPublicId(String publicId);

	List<SessionsEntity> findByUserId(UUID userId);

	Optional<SessionsEntity> findByRefreshToken(String refreshToken);

	void delete(String publicId);
}
