package com.auth.application.use_case.sessions;

import com.auth.domain.entities.Session;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByRefreshTokenQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.domain.services.validators.sessions.GetSessionsQueriesValidators;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultation des sessions.
 */
@Transactional(readOnly = true)
public class GetSessionsUseCase implements GetSessionsInterfacePort {

	private final SessionsRepositoryPort repository;

	public GetSessionsUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Optional<Session> findByPublicId(FindByPublicIdQuery query) {
		GetSessionsQueriesValidators.validate(query);
		return repository.findByPublicId(query.publicId());
	}

	@Override
	public List<Session> findByUserId(FindByUserIdQuery query) {
		GetSessionsQueriesValidators.validate(query);
		return repository.findByUserId(query.userId());
	}

	@Override
	public Optional<Session> findByRefreshToken(FindByRefreshTokenQuery query) {
		GetSessionsQueriesValidators.validate(query);
		return repository.findByRefreshToken(query.refreshToken());
	}
}
