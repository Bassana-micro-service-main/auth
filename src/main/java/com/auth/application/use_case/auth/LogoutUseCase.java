package com.auth.application.use_case.auth;

import com.auth.domain.entities.Session;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.LogoutInterfacePort;
import com.auth.domain.ports.in.auth.LogoutInterfacePort.LogoutCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.domain.services.validators.auth.AuthenticationValidators;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Révoque la session liée au refresh token.
 */
@Transactional
public class LogoutUseCase implements LogoutInterfacePort {

	private final SessionsRepositoryPort sessions;

	public LogoutUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort sessions) {
		this.sessions = sessions;
	}

	@Override
	public void logout(LogoutCommand command) {
		AuthenticationValidators.validate(command);
		Session session = sessions
				.findByRefreshToken(command.refreshToken())
				.orElseThrow(() -> new BusinessError(CodesError.AUTH_REFRESH_TOKEN_INVALID));
		if (session.isRevoked() || session.getExpiresAt().isBefore(Instant.now())) {
			throw new BusinessError(CodesError.AUTH_REFRESH_TOKEN_INVALID);
		}
		session.setRevoked(true);
		sessions.save(session);
	}
}
