package com.auth.application.dto.credentials;

import java.time.Instant;
import java.util.UUID;

/**
 * Réponse HTTP pour une ressource credentials (sans secrets ni identifiant interne).
 */
public record CredentialsResponseDto(
		String publicId,
		UUID userId,
		String email,
		boolean active,
		Instant passwordLastChangedAt,
		Instant createdAt,
		Instant updatedAt
) {
}
