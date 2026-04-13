package com.auth.application.use_case.tokens;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.DeleteTokensInterfacePort;
import com.auth.domain.ports.in.tokens.DeleteTokensInterfacePort.DeleteTokensCommand;
import com.auth.domain.ports.out.TokensRepositoryPort;
import com.auth.domain.services.validators.tokens.DeleteTokensValidators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Suppression d’un jeton.
 */
@Transactional
public class DeleteTokensUseCase implements DeleteTokensInterfacePort {

	private final TokensRepositoryPort repository;

	public DeleteTokensUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public void delete(DeleteTokensCommand command) {
		DeleteTokensValidators.validate(command);
		if (repository.findByPublicId(command.publicId()).isEmpty()) {
			throw new BusinessError(CodesError.TOKENS_NOT_FOUND);
		}
		repository.delete(command.publicId());
	}
}
