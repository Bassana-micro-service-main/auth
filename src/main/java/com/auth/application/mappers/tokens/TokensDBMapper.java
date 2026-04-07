package com.auth.application.mappers.tokens;

import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import com.auth.infrastructure.database.hibernate.entity.TokenEntity;

/**
 * Mapper entre l'entité de persistance Hibernate et l'entité de domaine jeton.
 */
public final class TokensDBMapper {

	private TokensDBMapper() {
	}

	public static Token toDomain(TokenEntity persistence) {
		if (persistence == null) {
			return null;
		}
		return new Token(
				persistence.getId(),
				persistence.getPublicId(),
				toDomainTokenType(persistence.getType()),
				persistence.getValue(),
				persistence.getExpiresAt(),
				persistence.getCreatedAt());
	}

	public static TokenEntity toPersistence(Token domain) {
		if (domain == null) {
			return null;
		}
		TokenEntity persistence = new TokenEntity();
		persistence.setId(domain.getId());
		persistence.setPublicId(domain.getPublicId());
		persistence.setType(toPersistenceTokenType(domain.getType()));
		persistence.setValue(domain.getValue());
		persistence.setExpiresAt(domain.getExpiresAt());
		persistence.setCreatedAt(domain.getCreatedAt());
		return persistence;
	}

	private static TokenType toDomainTokenType(com.auth.infrastructure.database.hibernate.enums.TokenType persistence) {
		if (persistence == null) {
			return null;
		}
		return TokenType.valueOf(persistence.name());
	}

	private static com.auth.infrastructure.database.hibernate.enums.TokenType toPersistenceTokenType(TokenType domain) {
		if (domain == null) {
			return null;
		}
		return com.auth.infrastructure.database.hibernate.enums.TokenType.valueOf(domain.name());
	}
}
