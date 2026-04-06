package com.auth.domain.ports.in.tokens;

import com.auth.domain.entities.TokensEntity;
import com.auth.domain.enums.TokenType;
import java.util.List;
import java.util.Optional;

/**
 * Port entrant (driving) : consultation d'entités {@link TokensEntity}.
 * Les critères passent uniquement par des requêtes ({@code Query}).
 */
public interface GetTokensInterfacePort {

	record FindByPublicIdQuery(String publicId) {
	}

	record FindByTypeQuery(TokenType type) {
	}

	record FindByValueQuery(String value) {
	}

	Optional<TokensEntity> findByPublicId(FindByPublicIdQuery query);

	List<TokensEntity> findByType(FindByTypeQuery query);

	Optional<TokensEntity> findByValue(FindByValueQuery query);
}
