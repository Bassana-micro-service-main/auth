package com.auth.application.use_case.sessions;

import com.auth.domain.entities.Session;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort.CreateSessionsCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.domain.services.validators.sessions.CreateSessionsValidators;
import com.auth.lib.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Création d’une session.
 */
@Transactional
public class CreateSessionsUseCase implements CreateSessionsInterfacePort {

	private final SessionsRepositoryPort repository;

	public CreateSessionsUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Session create(CreateSessionsCommand command) {
		CreateSessionsValidators.validate(command);
		Session session = new Session();
		session.setPublicId(Utils.newNanoid());
		session.setUserId(command.userId());
		session.setIpAddress(command.ipAddress());
		session.setUserAgent(command.userAgent());
		session.setDeviceName(command.deviceName());
		session.setRefreshToken(command.refreshToken());
		session.setExpiresAt(command.expiresAt());
		session.setRevoked(false);
		return repository.save(session);
	}
}
