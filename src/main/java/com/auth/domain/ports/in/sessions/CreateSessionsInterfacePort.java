package com.auth.domain.ports.in.sessions;

import com.auth.domain.entities.Session;
import java.time.Instant;
import java.util.UUID;

/**
 * Port entrant (driving) : création d'une entité {@link Session}.
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

	Session create(CreateSessionsCommand command);
}
