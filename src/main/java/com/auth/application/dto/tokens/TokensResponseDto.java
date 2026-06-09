package com.auth.application.dto.tokens;

import com.auth.domain.enums.TokenType;
import java.time.Instant;

/**
 * Réponse HTTP pour un jeton (sans valeur brute).
 */
public record TokensResponseDto(
		String publicId,
		TokenType type,
		Instant expiresAt,
		Instant createdAt
) {
}
