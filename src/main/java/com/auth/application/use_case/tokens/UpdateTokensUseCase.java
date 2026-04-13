package com.auth.application.use_case.tokens;

import com.auth.domain.entities.Token;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort.UpdateTokensCommand;
import com.auth.domain.ports.out.TokensRepositoryPort;
import com.auth.domain.services.validators.tokens.UpdateTokensValidators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mise à jour d’un jeton.
 */
@Transactional
public class UpdateTokensUseCase implements UpdateTokensInterfacePort {

	private final TokensRepositoryPort repository;

	public UpdateTokensUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Token update(UpdateTokensCommand command) {
		UpdateTokensValidators.validate(command);
		Token token = repository
				.findByPublicId(command.publicId())
				.orElseThrow(() -> new BusinessError(CodesError.TOKENS_NOT_FOUND));
		command.value().ifPresent(token::setValue);
		command.expiresAt().ifPresent(token::setExpiresAt);
		return repository.save(token);
	}
}
