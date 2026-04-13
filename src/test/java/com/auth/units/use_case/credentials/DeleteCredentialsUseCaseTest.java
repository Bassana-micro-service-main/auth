package com.auth.units.use_case.credentials;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.credentials.DeleteCredentialsUseCase;
import com.auth.domain.entities.Credential;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort.DeleteCredentialsCommand;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCredentialsUseCase")
class DeleteCredentialsUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";

	@Mock
	private CredentialsRepositoryPort repository;

	private DeleteCredentialsUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new DeleteCredentialsUseCase(repository);
	}

	@Test
	void shouldDeleteWhenExists() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(new Credential()));

		useCase.delete(new DeleteCredentialsCommand(PUBLIC_ID));

		verify(repository).delete(PUBLIC_ID);
	}

	@Test
	void shouldThrowWhenNotFound() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.delete(new DeleteCredentialsCommand(PUBLIC_ID)));
		assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_NOT_FOUND);
		verify(repository, never()).delete(any());
	}

	@Test
	void invalidPublicId_throwsFromValidator() {
		assertThrows(BusinessError.class, () -> useCase.delete(new DeleteCredentialsCommand("x")));
		verify(repository, never()).findByPublicId(any());
	}
}
