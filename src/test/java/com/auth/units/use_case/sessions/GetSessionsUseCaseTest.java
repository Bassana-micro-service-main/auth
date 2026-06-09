package com.auth.units.use_case.sessions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.sessions.GetSessionsUseCase;
import com.auth.domain.entities.Session;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByRefreshTokenQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetSessionsUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Mock
	private SessionsRepositoryPort repository;

	private GetSessionsUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new GetSessionsUseCase(repository);
	}

	@Test
	void findByPublicId() {
		var s = new Session();
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(s));
		assertThat(useCase.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).contains(s);
	}

	@Test
	void findByUserId() {
		var s = new Session();
		when(repository.findByUserId(USER_ID)).thenReturn(List.of(s));
		assertThat(useCase.findByUserId(new FindByUserIdQuery(USER_ID))).containsExactly(s);
	}

	@Test
	void findByRefreshToken() {
		var s = new Session();
		when(repository.findByRefreshToken("tok")).thenReturn(Optional.of(s));
		assertThat(useCase.findByRefreshToken(new FindByRefreshTokenQuery("tok"))).contains(s);
	}

	@Test
	void invalidPublicId() {
		assertThrows(BusinessError.class, () -> useCase.findByPublicId(new FindByPublicIdQuery("x")));
		verify(repository, never()).findByPublicId(any());
	}

	@Test
	void nullUserId() {
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.findByUserId(new FindByUserIdQuery(null)));
		assertThat(ex.getCode()).isEqualTo(CodesError.SESSIONS_USER_ID_INVALID);
	}
}
