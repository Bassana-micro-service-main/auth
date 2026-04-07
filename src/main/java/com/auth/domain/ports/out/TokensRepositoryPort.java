package com.auth.domain.ports.out;

import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant (driven) : persistance des {@link Token}.
 */
public interface TokensRepositoryPort {

	String REPOSITORY_QUALIFIER = "tokensRepository";

	Token save(Token entity);

	Optional<Token> findById(UUID id);

	Optional<Token> findByPublicId(String publicId);

	List<Token> findByType(TokenType type);

	Optional<Token> findByValue(String value);

	void delete(String publicId);
}
