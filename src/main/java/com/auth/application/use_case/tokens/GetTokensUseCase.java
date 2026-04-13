package com.auth.application.use_case.tokens;

import com.auth.domain.entities.Token;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByTypeQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByValueQuery;
import com.auth.domain.ports.out.TokensRepositoryPort;
import com.auth.domain.services.validators.tokens.GetTokensQueriesValidators;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultation des jetons.
 */
@Transactional(readOnly = true)
public class GetTokensUseCase implements GetTokensInterfacePort {

	private final TokensRepositoryPort repository;

	public GetTokensUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Optional<Token> findByPublicId(FindByPublicIdQuery query) {
		GetTokensQueriesValidators.validate(query);
		return repository.findByPublicId(query.publicId());
	}

	@Override
	public List<Token> findByType(FindByTypeQuery query) {
		GetTokensQueriesValidators.validate(query);
		return repository.findByType(query.type());
	}

	@Override
	public Optional<Token> findByValue(FindByValueQuery query) {
		GetTokensQueriesValidators.validate(query);
		return repository.findByValue(query.value());
	}
}
