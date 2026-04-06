package com.auth.domain.ports.in.sessions;

import com.auth.domain.entities.SessionsEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port entrant (driving) : consultation d'entités {@link SessionsEntity}.
 * Les critères passent uniquement par des requêtes ({@code Query}).
 */
public interface GetSessionsInterfacePort {

	record FindByPublicIdQuery(String publicId) {
	}

	record FindByUserIdQuery(UUID userId) {
	}

	record FindByRefreshTokenQuery(String refreshToken) {
	}

	Optional<SessionsEntity> findByPublicId(FindByPublicIdQuery query);

	List<SessionsEntity> findByUserId(FindByUserIdQuery query);

	Optional<SessionsEntity> findByRefreshToken(FindByRefreshTokenQuery query);
}
