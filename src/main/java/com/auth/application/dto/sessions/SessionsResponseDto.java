package com.auth.application.dto.sessions;

import java.time.Instant;
import java.util.UUID;

/**
 * Réponse HTTP pour une session (sans refresh token).
 */
public record SessionsResponseDto(
		String publicId,
		UUID userId,
		String ipAddress,
		String userAgent,
		String deviceName,
		Instant expiresAt,
		boolean revoked,
		Instant createdAt,
		Instant updatedAt
) {
}
