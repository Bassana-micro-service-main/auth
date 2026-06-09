package com.auth.application.dto.tokens;

import java.time.Instant;
import java.util.Optional;

/**
 * DTO application pour {@link com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort.UpdateTokensCommand}.
 */
public record UpdateTokensDto(
		String publicId,
		Optional<String> value,
		Optional<Instant> expiresAt
) {
}
