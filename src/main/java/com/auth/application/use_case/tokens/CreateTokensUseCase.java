package com.auth.application.use_case.tokens;

import com.auth.domain.entities.Token;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort.CreateTokensCommand;
import com.auth.domain.ports.out.TokensRepositoryPort;
import com.auth.domain.services.validators.tokens.CreateTokensValidators;
import com.auth.lib.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Création d’un jeton.
 */
@Transactional
public class CreateTokensUseCase implements CreateTokensInterfacePort {

	private final TokensRepositoryPort repository;

	public CreateTokensUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Token create(CreateTokensCommand command) {
		CreateTokensValidators.validate(command);
		Token token = new Token();
		token.setPublicId(Utils.newNanoid());
		token.setType(command.type());
		token.setValue(command.value());
		token.setExpiresAt(command.expiresAt());
		return repository.save(token);
	}
}
