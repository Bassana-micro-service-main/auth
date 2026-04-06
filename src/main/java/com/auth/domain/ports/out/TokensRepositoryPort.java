package com.auth.domain.ports.out;

import com.auth.domain.entities.TokensEntity;
import com.auth.domain.enums.TokenType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant (driven) : persistance des {@link TokensEntity}.
 */
public interface TokensRepositoryPort {

	String REPOSITORY_QUALIFIER = "tokensRepository";

	TokensEntity save(TokensEntity entity);

	Optional<TokensEntity> findById(UUID id);

	Optional<TokensEntity> findByPublicId(String publicId);

	List<TokensEntity> findByType(TokenType type);

	Optional<TokensEntity> findByValue(String value);

	void delete(String publicId);
}
