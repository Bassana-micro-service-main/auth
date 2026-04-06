package com.auth.domain.ports.in.sessions;

import com.auth.domain.entities.SessionsEntity;
import java.time.Instant;
import java.util.UUID;

/**
 * Port entrant (driving) : création d'une entité {@link SessionsEntity}.
 */
public interface CreateSessionsInterfacePort {

	record CreateSessionsCommand(
			UUID userId,
			String ipAddress,
			String userAgent,
			String deviceName,
			String refreshToken,
			Instant expiresAt
	) {
	}

	SessionsEntity create(CreateSessionsCommand command);
}
