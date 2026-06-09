package com.auth.domain.ports.in.sessions;

import com.auth.domain.entities.Session;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port entrant (driving) : consultation d'entités {@link Session}.
 * Les critères passent uniquement par des requêtes ({@code Query}).
 */
public interface GetSessionsInterfacePort {

	record FindByPublicIdQuery(String publicId) {
	}

	record FindByUserIdQuery(UUID userId) {
	}

	record FindByRefreshTokenQuery(String refreshToken) {
	}

	Optional<Session> findByPublicId(FindByPublicIdQuery query);

	List<Session> findByUserId(FindByUserIdQuery query);

	Optional<Session> findByRefreshToken(FindByRefreshTokenQuery query);
}
