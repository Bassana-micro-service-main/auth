package com.auth.application.use_case.sessions;

import com.auth.domain.entities.Session;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort.UpdateSessionsCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.domain.services.validators.sessions.UpdateSessionsValidators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mise à jour d’une session.
 */
@Transactional
public class UpdateSessionsUseCase implements UpdateSessionsInterfacePort {

	private final SessionsRepositoryPort repository;

	public UpdateSessionsUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Session update(UpdateSessionsCommand command) {
		UpdateSessionsValidators.validate(command);
		Session session = repository
				.findByPublicId(command.publicId())
				.orElseThrow(() -> new BusinessError(CodesError.SESSIONS_NOT_FOUND));
		command.ipAddress().ifPresent(session::setIpAddress);
		command.userAgent().ifPresent(session::setUserAgent);
		command.deviceName().ifPresent(session::setDeviceName);
		command.refreshToken().ifPresent(session::setRefreshToken);
		command.expiresAt().ifPresent(session::setExpiresAt);
		command.revoked().ifPresent(session::setRevoked);
		return repository.save(session);
	}
}
