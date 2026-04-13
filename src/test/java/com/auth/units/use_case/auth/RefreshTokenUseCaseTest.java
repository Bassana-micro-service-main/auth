package com.auth.units.use_case.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.auth.application.auth.AuthSessionIssuer;
import com.auth.application.use_case.auth.RefreshTokenUseCase;
import com.auth.domain.entities.Session;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.AuthSessionResult;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort.RefreshTokenCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

	@Mock
	private SessionsRepositoryPort sessions;

	@Mock
	private AuthSessionIssuer authSessionIssuer;

	private RefreshTokenUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new RefreshTokenUseCase(sessions, authSessionIssuer);
	}

	private Session validSession() {
		var s = new Session();
		s.setPublicId("abcdefghij12345678901");
		s.setRefreshToken("opaque-rt");
		s.setRevoked(false);
		s.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
		return s;
	}

	@Test
	void shouldIssueNewAccessToken() {
		var session = validSession();
		when(sessions.findByRefreshToken("opaque-rt")).thenReturn(Optional.of(session));
		var result = new AuthSessionResult("new-at", "opaque-rt", Instant.now(), session.getExpiresAt(), session.getPublicId());
		when(authSessionIssuer.issueNewAccessToken(eq(session), eq("opaque-rt"))).thenReturn(result);

		assertThat(useCase.refresh(new RefreshTokenCommand("opaque-rt"))).isEqualTo(result);
	}

	@Test
	void unknownToken() {
		when(sessions.findByRefreshToken("x")).thenReturn(Optional.empty());
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.refresh(new RefreshTokenCommand("x")));
		assertThat(ex.getCode()).isEqualTo(CodesError.AUTH_REFRESH_TOKEN_INVALID);
	}

	@Test
	void revokedSession() {
		var s = validSession();
		s.setRevoked(true);
		when(sessions.findByRefreshToken("opaque-rt")).thenReturn(Optional.of(s));
		BusinessError ex =
				assertThrows(BusinessError.class, () -> useCase.refresh(new RefreshTokenCommand("opaque-rt")));
		assertThat(ex.getCode()).isEqualTo(CodesError.AUTH_REFRESH_TOKEN_INVALID);
	}
}
