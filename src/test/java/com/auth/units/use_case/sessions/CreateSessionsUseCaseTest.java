package com.auth.units.use_case.sessions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.sessions.CreateSessionsUseCase;
import com.auth.domain.entities.Session;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort.CreateSessionsCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.lib.Utils;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateSessionsUseCase")
class CreateSessionsUseCaseTest {

	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
	private static final Instant FUTURE = Instant.now().plus(7, ChronoUnit.DAYS);

	@Mock
	private SessionsRepositoryPort repository;

	private CreateSessionsUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new CreateSessionsUseCase(repository);
	}

	private static CreateSessionsCommand validCommand() {
		return new CreateSessionsCommand(USER_ID, "10.0.0.1", "UA", "Chrome", "rt", FUTURE);
	}

	@Test
	void shouldValidateAssignNanoidAndSave() {
		var cmd = validCommand();
		when(repository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

		Session result = useCase.create(cmd);

		ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
		verify(repository).save(captor.capture());
		assertThat(captor.getValue().getPublicId()).matches(Utils.NANOID_REGEX.pattern());
		assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
		assertThat(captor.getValue().isRevoked()).isFalse();
		assertThat(result).isSameAs(captor.getValue());
	}

	@Test
	void validationFailure_neverSaves() {
		var cmd = new CreateSessionsCommand(null, "1.1.1.1", "ua", "d", "rt", FUTURE);
		assertThrows(BusinessError.class, () -> useCase.create(cmd));
		verify(repository, never()).save(any());
	}

	@Test
	void savePropagates() {
		when(repository.save(any(Session.class))).thenThrow(new RuntimeException("fail"));
		assertThrows(RuntimeException.class, () -> useCase.create(validCommand()));
	}
}
