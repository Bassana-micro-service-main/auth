package com.auth.application.dto.sessions;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO application pour {@link com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort.CreateSessionsCommand}.
 */
public record CreateSessionsDto(
		UUID userId,
		String ipAddress,
		String userAgent,
		String deviceName,
		String refreshToken,
		Instant expiresAt
) {
}
