package com.auth.units.use_case.credentials;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.credentials.GetCredentialsUseCase;
import com.auth.domain.entities.Credential;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByEmailQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCredentialsUseCase")
class GetCredentialsUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Mock
	private CredentialsRepositoryPort repository;

	private GetCredentialsUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new GetCredentialsUseCase(repository);
	}

	@Test
	void findByPublicId_delegatesAfterValidation() {
		var cred = new Credential();
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(cred));

		assertThat(useCase.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).contains(cred);
		verify(repository).findByPublicId(PUBLIC_ID);
	}

	@Test
	void findByEmail_delegatesAfterValidation() {
		var cred = new Credential();
		when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(cred));

		assertThat(useCase.findByEmail(new FindByEmailQuery("a@b.com"))).contains(cred);
	}

	@Test
	void findByUserId_delegatesAfterValidation() {
		var cred = new Credential();
		when(repository.findByUserId(USER_ID)).thenReturn(Optional.of(cred));

		assertThat(useCase.findByUserId(new FindByUserIdQuery(USER_ID))).contains(cred);
	}

	@Test
	void invalidPublicId_throwsBeforeRepository() {
		assertThrows(BusinessError.class, () -> useCase.findByPublicId(new FindByPublicIdQuery("bad")));
		verify(repository, never()).findByPublicId(any());
	}

	@Test
	void invalidEmail_throwsBeforeRepository() {
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.findByEmail(new FindByEmailQuery("x")));
		assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_EMAIL_INVALID);
		verify(repository, never()).findByEmail(any());
	}

	@Test
	void nullUserId_throwsBeforeRepository() {
		BusinessError ex =
				assertThrows(BusinessError.class, () -> useCase.findByUserId(new FindByUserIdQuery(null)));
		assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_USER_ID_INVALID);
		verify(repository, never()).findByUserId(any());
	}
}
