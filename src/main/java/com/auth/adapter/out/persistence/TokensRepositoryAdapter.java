package com.auth.adapter.out.persistence;

import com.auth.application.mappers.tokens.TokensDBMapper;
import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import com.auth.domain.ports.out.TokensRepositoryPort;
import com.auth.infrastructure.database.hibernate.entity.TokenEntity;
import com.auth.infrastructure.database.hibernate.repository.TokenEntityRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * Adaptateur de persistance pour {@link TokensRepositoryPort} (équivalent NestJS repository + ORM).
 * Délègue à {@link TokenEntityRepository} et mappe via {@link TokensDBMapper}.
 */
@RequiredArgsConstructor
public class TokensRepositoryAdapter implements TokensRepositoryPort {

	private final TokenEntityRepository jpa;

	@Override
	public Token save(Token entity) {
		TokenEntity persistence = TokensDBMapper.toPersistence(entity);
		if (entity.getPublicId() != null) {
			jpa.findByPublicId(entity.getPublicId()).ifPresent(existing -> {
				persistence.setId(existing.getId());
				if (persistence.getCreatedAt() == null) {
					persistence.setCreatedAt(existing.getCreatedAt());
				}
			});
		}
		return TokensDBMapper.toDomain(jpa.save(persistence));
	}

	@Override
	public Optional<Token> findById(UUID id) {
		return jpa.findById(id).map(TokensDBMapper::toDomain);
	}

	@Override
	public Optional<Token> findByPublicId(String publicId) {
		return jpa.findByPublicId(publicId).map(TokensDBMapper::toDomain);
	}

	@Override
	public List<Token> findByType(TokenType type) {
		return jpa.findByType(toPersistenceTokenType(type)).stream().map(TokensDBMapper::toDomain).toList();
	}

	@Override
	public Optional<Token> findByValue(String value) {
		return jpa.findByValue(value).map(TokensDBMapper::toDomain);
	}

	@Override
	public void delete(String publicId) {
		jpa.findByPublicId(publicId).ifPresent(jpa::delete);
	}

	private static com.auth.infrastructure.database.hibernate.enums.TokenType toPersistenceTokenType(TokenType domain) {
		if (domain == null) {
			return null;
		}
		return com.auth.infrastructure.database.hibernate.enums.TokenType.valueOf(domain.name());
	}
}
