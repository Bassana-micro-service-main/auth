package com.auth.application.dto.tokens;

import com.auth.domain.enums.TokenType;
import java.time.Instant;

/**
 * DTO application pour {@link com.auth.domain.ports.in.tokens.CreateTokensInterfacePort.CreateTokensCommand}.
 */
public record CreateTokensDto(
		TokenType type,
		String value,
		Instant expiresAt
) {
}
