package com.auth.domain.ports.in.tokens;

import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import java.time.Instant;

/**
 * Port entrant (driving) : création d'une entité {@link Token}.
 */
public interface CreateTokensInterfacePort {

	record CreateTokensCommand(
			TokenType type,
			String value,
			Instant expiresAt
	) {
	}

	Token create(CreateTokensCommand command);
}
