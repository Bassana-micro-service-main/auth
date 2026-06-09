package com.auth.units.use_case.sessions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.sessions.UpdateSessionsUseCase;
import com.auth.domain.entities.Session;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort.UpdateSessionsCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateSessionsUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";

	@Mock
	private SessionsRepositoryPort repository;

	private UpdateSessionsUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new UpdateSessionsUseCase(repository);
	}

	private Session existing() {
		var s = new Session();
		s.setPublicId(PUBLIC_ID);
		s.setRevoked(false);
		return s;
	}

	@Test
	void shouldUpdateAndSave() {
		var session = existing();
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(session));
		when(repository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

		var cmd = new UpdateSessionsCommand(
				PUBLIC_ID,
				Optional.empty(),
				Optional.empty(),
				Optional.of("new-device"),
				Optional.empty(),
				Optional.empty(),
				Optional.empty());

		Session out = useCase.update(cmd);
		assertThat(out.getDeviceName()).isEqualTo("new-device");
	}

	@Test
	void notFound() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
		var cmd = new UpdateSessionsCommand(
				PUBLIC_ID,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty());
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.update(cmd));
		assertThat(ex.getCode()).isEqualTo(CodesError.SESSIONS_NOT_FOUND);
	}
}
