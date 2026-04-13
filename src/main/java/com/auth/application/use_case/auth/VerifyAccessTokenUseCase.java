package com.auth.application.use_case.auth;

import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.VerifyAccessTokenInterfacePort;
import com.auth.domain.ports.out.TokensRepositoryPort;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Vérifie qu'un bearer token correspond à un access token actif.
 */
public class VerifyAccessTokenUseCase implements VerifyAccessTokenInterfacePort {

	private static final String BEARER_PREFIX = "Bearer ";

	private final TokensRepositoryPort tokens;

	public VerifyAccessTokenUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort tokens) {
		this.tokens = tokens;
	}

	@Override
	public void verify(VerifyAccessTokenCommand command) {
		String value = extractBearerToken(command.authorizationHeader());

		Token token = tokens.findByValue(value).orElseThrow(() -> new BusinessError(CodesError.AUTH_INVALID_CREDENTIALS));
		if (token.getType() != TokenType.ACCESS) {
			throw new BusinessError(CodesError.AUTH_INVALID_CREDENTIALS);
		}
		if (token.getExpiresAt() == null || !token.getExpiresAt().isAfter(Instant.now())) {
			throw new BusinessError(CodesError.AUTH_INVALID_CREDENTIALS);
		}
	}

	private String extractBearerToken(String authorizationHeader) {
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new BusinessError(CodesError.AUTH_INVALID_CREDENTIALS);
		}
		if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
			throw new BusinessError(CodesError.AUTH_INVALID_CREDENTIALS);
		}
		String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
		if (token.isBlank()) {
			throw new BusinessError(CodesError.AUTH_INVALID_CREDENTIALS);
		}
		return token;
	}
}
