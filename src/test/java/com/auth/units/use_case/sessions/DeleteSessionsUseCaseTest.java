package com.auth.units.use_case.sessions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.sessions.DeleteSessionsUseCase;
import com.auth.domain.entities.Session;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort.DeleteSessionsCommand;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteSessionsUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";

	@Mock
	private SessionsRepositoryPort repository;

	private DeleteSessionsUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new DeleteSessionsUseCase(repository);
	}

	@Test
	void deletesWhenPresent() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(new Session()));
		useCase.delete(new DeleteSessionsCommand(PUBLIC_ID));
		verify(repository).delete(PUBLIC_ID);
	}

	@Test
	void notFound() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.delete(new DeleteSessionsCommand(PUBLIC_ID)));
		assertThat(ex.getCode()).isEqualTo(CodesError.SESSIONS_NOT_FOUND);
		verify(repository, never()).delete(any());
	}
}
