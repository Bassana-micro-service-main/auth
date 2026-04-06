package com.auth.domain.ports.in.tokens;

import com.auth.domain.entities.TokensEntity;
import com.auth.domain.enums.TokenType;
import java.time.Instant;

/**
 * Port entrant (driving) : création d'une entité {@link TokensEntity}.
 */
public interface CreateTokensInterfacePort {

	record CreateTokensCommand(
			TokenType type,
			String value,
			Instant expiresAt
	) {
	}

	TokensEntity create(CreateTokensCommand command);
}
