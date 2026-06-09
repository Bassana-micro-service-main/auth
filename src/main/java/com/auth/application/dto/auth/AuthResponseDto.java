package com.auth.application.dto.auth;

import java.time.Instant;

/**
 * Réponse login / register / refresh.
 */
public record AuthResponseDto(
		String accessToken,
		String refreshToken,
		Instant accessExpiresAt,
		Instant refreshExpiresAt,
		String sessionPublicId
) {
}
