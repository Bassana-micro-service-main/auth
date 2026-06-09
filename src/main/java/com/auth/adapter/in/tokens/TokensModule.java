package com.auth.adapter.in.tokens;

import com.auth.adapter.out.persistence.TokensRepositoryAdapter;
import com.auth.application.use_case.tokens.CreateTokensUseCase;
import com.auth.application.use_case.tokens.DeleteTokensUseCase;
import com.auth.application.use_case.tokens.GetTokensUseCase;
import com.auth.application.use_case.tokens.UpdateTokensUseCase;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort;
import com.auth.domain.ports.in.tokens.DeleteTokensInterfacePort;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort;
import com.auth.domain.ports.out.TokensRepositoryPort;
import com.auth.infrastructure.database.hibernate.repository.TokenEntityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Module tokens (équivalent NestJS {@code @Module}).
 */
@Configuration
@Import(TokensControllerAdapter.class)
public class TokensModule {

	@Bean(name = TokensRepositoryPort.REPOSITORY_QUALIFIER)
	public TokensRepositoryPort tokensRepositoryPort(TokenEntityRepository jpa) {
		return new TokensRepositoryAdapter(jpa);
	}

	@Bean
	public CreateTokensInterfacePort createTokensUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort repository) {
		return new CreateTokensUseCase(repository);
	}

	@Bean
	public GetTokensInterfacePort getTokensUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort repository) {
		return new GetTokensUseCase(repository);
	}

	@Bean
	public UpdateTokensInterfacePort updateTokensUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort repository) {
		return new UpdateTokensUseCase(repository);
	}

	@Bean
	public DeleteTokensInterfacePort deleteTokensUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort repository) {
		return new DeleteTokensUseCase(repository);
	}
}
