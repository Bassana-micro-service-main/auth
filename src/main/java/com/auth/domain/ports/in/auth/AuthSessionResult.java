package com.auth.domain.ports.in.auth;

import java.time.Instant;

/**
 * Résultat des flux login / register / refresh : jetons opaques et métadonnées exposables côté API.
 */
public record AuthSessionResult(
		String accessToken,
		String refreshToken,
		Instant accessExpiresAt,
		Instant refreshExpiresAt,
		String sessionPublicId
) {
}
