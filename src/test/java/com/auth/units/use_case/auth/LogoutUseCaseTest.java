package com.auth.units.use_case.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.auth.LogoutUseCase;
import com.auth.domain.entities.Session;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.LogoutInterfacePort.LogoutCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

	@Mock
	private SessionsRepositoryPort sessions;

	private LogoutUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new LogoutUseCase(sessions);
	}

	private Session validSession() {
		var s = new Session();
		s.setRefreshToken("opaque-rt");
		s.setRevoked(false);
		s.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
		return s;
	}

	@Test
	void shouldRevokeSession() {
		var session = validSession();
		when(sessions.findByRefreshToken("opaque-rt")).thenReturn(Optional.of(session));
		when(sessions.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

		useCase.logout(new LogoutCommand("opaque-rt"));

		ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
		verify(sessions).save(captor.capture());
		assertThat(captor.getValue().isRevoked()).isTrue();
	}

	@Test
	void unknownRefreshToken() {
		when(sessions.findByRefreshToken("x")).thenReturn(Optional.empty());
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.logout(new LogoutCommand("x")));
		assertThat(ex.getCode()).isEqualTo(CodesError.AUTH_REFRESH_TOKEN_INVALID);
	}

	@Test
	void alreadyRevoked() {
		var s = validSession();
		s.setRevoked(true);
		when(sessions.findByRefreshToken("opaque-rt")).thenReturn(Optional.of(s));
		BusinessError ex =
				assertThrows(BusinessError.class, () -> useCase.logout(new LogoutCommand("opaque-rt")));
		assertThat(ex.getCode()).isEqualTo(CodesError.AUTH_REFRESH_TOKEN_INVALID);
	}

	@Test
	void expiredSession() {
		var s = validSession();
		s.setExpiresAt(Instant.now().minus(1, ChronoUnit.HOURS));
		when(sessions.findByRefreshToken("opaque-rt")).thenReturn(Optional.of(s));
		BusinessError ex =
				assertThrows(BusinessError.class, () -> useCase.logout(new LogoutCommand("opaque-rt")));
		assertThat(ex.getCode()).isEqualTo(CodesError.AUTH_REFRESH_TOKEN_INVALID);
	}
}
