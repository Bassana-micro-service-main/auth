package com.auth.application.use_case.sessions;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort.DeleteSessionsCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.domain.services.validators.sessions.DeleteSessionsValidators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Suppression d’une session.
 */
@Transactional
public class DeleteSessionsUseCase implements DeleteSessionsInterfacePort {

	private final SessionsRepositoryPort repository;

	public DeleteSessionsUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public void delete(DeleteSessionsCommand command) {
		DeleteSessionsValidators.validate(command);
		if (repository.findByPublicId(command.publicId()).isEmpty()) {
			throw new BusinessError(CodesError.SESSIONS_NOT_FOUND);
		}
		repository.delete(command.publicId());
	}
}
