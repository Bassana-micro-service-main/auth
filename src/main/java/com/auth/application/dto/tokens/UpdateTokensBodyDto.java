package com.auth.application.dto.tokens;

import java.time.Instant;
import java.util.Optional;

/**
 * Corps PATCH : le {@code publicId} est dans le chemin.
 */
public record UpdateTokensBodyDto(
		Optional<String> value,
		Optional<Instant> expiresAt
) {
	public UpdateTokensBodyDto {
		value = value != null ? value : Optional.empty();
		expiresAt = expiresAt != null ? expiresAt : Optional.empty();
	}
}
