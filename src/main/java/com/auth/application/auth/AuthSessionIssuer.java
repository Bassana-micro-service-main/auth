package com.auth.application.auth;

import com.auth.domain.entities.Session;
import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import com.auth.domain.ports.in.auth.AuthSessionResult;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.domain.ports.out.TokensRepositoryPort;
import com.auth.infrastructure.security.AuthTokenProperties;
import com.auth.lib.Utils;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Émet une session + paire access / refresh tokens (logique partagée login / register).
 */
@Component
public class AuthSessionIssuer {

	private final SessionsRepositoryPort sessions;
	private final TokensRepositoryPort tokens;
	private final AuthTokenProperties timing;

	public AuthSessionIssuer(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort sessions,
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort tokens,
			AuthTokenProperties timing) {
		this.sessions = sessions;
		this.tokens = tokens;
		this.timing = timing;
	}

	public AuthSessionResult issueSessionAndTokens(
			UUID userId, String ipAddress, String userAgent, String deviceName) {
		Instant now = Instant.now();
		String accessValue = Utils.newOpaqueToken(32);
		String refreshValue = Utils.newOpaqueToken(32);
		Instant accessExp = now.plus(timing.getAccessTokenMinutes(), ChronoUnit.MINUTES);
		Instant refreshExp = now.plus(timing.getRefreshTokenDays(), ChronoUnit.DAYS);

		Session session = new Session();
		session.setPublicId(Utils.newNanoid());
		session.setUserId(userId);
		session.setIpAddress(ipAddress != null ? ipAddress : "");
		session.setUserAgent(userAgent != null ? userAgent : "");
		session.setDeviceName(
				deviceName != null && !deviceName.isBlank() ? deviceName : "unknown");
		session.setRefreshToken(refreshValue);
		session.setExpiresAt(refreshExp);
		session.setRevoked(false);
		session = sessions.save(session);

		Token access = new Token();
		access.setPublicId(Utils.newNanoid());
		access.setType(TokenType.ACCESS);
		access.setValue(accessValue);
		access.setExpiresAt(accessExp);
		tokens.save(access);

		Token refresh = new Token();
		refresh.setPublicId(Utils.newNanoid());
		refresh.setType(TokenType.REFRESH);
		refresh.setValue(refreshValue);
		refresh.setExpiresAt(refreshExp);
		tokens.save(refresh);

		return new AuthSessionResult(
				accessValue, refreshValue, accessExp, refreshExp, session.getPublicId());
	}

	/**
	 * Émet uniquement un nouvel access token (refresh inchangé).
	 */
	public AuthSessionResult issueNewAccessToken(Session session, String refreshToken) {
		Instant now = Instant.now();
		String accessValue = Utils.newOpaqueToken(32);
		Instant accessExp = now.plus(timing.getAccessTokenMinutes(), ChronoUnit.MINUTES);

		Token access = new Token();
		access.setPublicId(Utils.newNanoid());
		access.setType(TokenType.ACCESS);
		access.setValue(accessValue);
		access.setExpiresAt(accessExp);
		tokens.save(access);

		return new AuthSessionResult(
				accessValue,
				refreshToken,
				accessExp,
				session.getExpiresAt(),
				session.getPublicId());
	}
}
