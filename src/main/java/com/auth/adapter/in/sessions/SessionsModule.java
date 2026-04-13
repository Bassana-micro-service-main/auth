package com.auth.adapter.in.sessions;

import com.auth.adapter.out.persistence.SessionsRepositoryAdapter;
import com.auth.application.use_case.sessions.CreateSessionsUseCase;
import com.auth.application.use_case.sessions.DeleteSessionsUseCase;
import com.auth.application.use_case.sessions.GetSessionsUseCase;
import com.auth.application.use_case.sessions.UpdateSessionsUseCase;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.infrastructure.database.hibernate.repository.SessionEntityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Module sessions (équivalent NestJS {@code @Module}).
 */
@Configuration
@Import(SessionsControllerAdapter.class)
public class SessionsModule {

	@Bean(name = SessionsRepositoryPort.REPOSITORY_QUALIFIER)
	public SessionsRepositoryPort sessionsRepositoryPort(SessionEntityRepository jpa) {
		return new SessionsRepositoryAdapter(jpa);
	}

	@Bean
	public CreateSessionsInterfacePort createSessionsUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort repository) {
		return new CreateSessionsUseCase(repository);
	}

	@Bean
	public GetSessionsInterfacePort getSessionsUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort repository) {
		return new GetSessionsUseCase(repository);
	}

	@Bean
	public UpdateSessionsInterfacePort updateSessionsUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort repository) {
		return new UpdateSessionsUseCase(repository);
	}

	@Bean
	public DeleteSessionsInterfacePort deleteSessionsUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort repository) {
		return new DeleteSessionsUseCase(repository);
	}
}
