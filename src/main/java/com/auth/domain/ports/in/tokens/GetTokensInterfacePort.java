package com.auth.domain.ports.in.tokens;

import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import java.util.List;
import java.util.Optional;

/**
 * Port entrant (driving) : consultation d'entités {@link Token}.
 * Les critères passent uniquement par des requêtes ({@code Query}).
 */
public interface GetTokensInterfacePort {

	record FindByPublicIdQuery(String publicId) {
	}

	record FindByTypeQuery(TokenType type) {
	}

	record FindByValueQuery(String value) {
	}

	Optional<Token> findByPublicId(FindByPublicIdQuery query);

	List<Token> findByType(FindByTypeQuery query);

	Optional<Token> findByValue(FindByValueQuery query);
}
