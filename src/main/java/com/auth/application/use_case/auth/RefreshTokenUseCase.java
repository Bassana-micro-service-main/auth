package com.auth.application.use_case.auth;

import com.auth.application.auth.AuthSessionIssuer;
import com.auth.domain.entities.Session;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.AuthSessionResult;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort.RefreshTokenCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.domain.services.validators.auth.AuthenticationValidators;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Émet un nouvel access token à partir d’un refresh token valide.
 */
@Transactional
public class RefreshTokenUseCase implements RefreshTokenInterfacePort {

	private final SessionsRepositoryPort sessions;
	private final AuthSessionIssuer authSessionIssuer;

	public RefreshTokenUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort sessions,
			AuthSessionIssuer authSessionIssuer) {
		this.sessions = sessions;
		this.authSessionIssuer = authSessionIssuer;
	}

	@Override
	public AuthSessionResult refresh(RefreshTokenCommand command) {
		AuthenticationValidators.validate(command);
		Session session = sessions
				.findByRefreshToken(command.refreshToken())
				.orElseThrow(() -> new BusinessError(CodesError.AUTH_REFRESH_TOKEN_INVALID));
		if (session.isRevoked() || session.getExpiresAt().isBefore(Instant.now())) {
			throw new BusinessError(CodesError.AUTH_REFRESH_TOKEN_INVALID);
		}
		if (!session.getRefreshToken().equals(command.refreshToken())) {
			throw new BusinessError(CodesError.AUTH_REFRESH_TOKEN_INVALID);
		}
		return authSessionIssuer.issueNewAccessToken(session, command.refreshToken());
	}
}
