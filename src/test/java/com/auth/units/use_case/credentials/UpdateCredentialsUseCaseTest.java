package com.auth.units.use_case.credentials;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.credentials.UpdateCredentialsUseCase;
import com.auth.domain.entities.Credential;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort.UpdateCredentialsCommand;
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
@DisplayName("UpdateCredentialsUseCase")
class UpdateCredentialsUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	private static final String HASH_A =
			"$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
	private static final String HASH_B =
			"$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

	@Mock
	private CredentialsRepositoryPort repository;

	private UpdateCredentialsUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new UpdateCredentialsUseCase(repository);
	}

	private Credential existing() {
		var c = new Credential();
		c.setPublicId(PUBLIC_ID);
		c.setUserId(USER_ID);
		c.setEmail("old@example.com");
		c.setHashedPassword(HASH_A);
		c.setActive(true);
		return c;
	}

	@Test
	void shouldUpdateFieldsAndSave() {
		var cred = existing();
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(cred));
		when(repository.save(any(Credential.class))).thenAnswer(inv -> inv.getArgument(0));

		var cmd = new UpdateCredentialsCommand(
				PUBLIC_ID, Optional.of("new@example.com"), Optional.of(HASH_B), Optional.empty(), Optional.empty());

		Credential result = useCase.update(cmd);

		assertThat(result.getEmail()).isEqualTo("new@example.com");
		assertThat(result.getHashedPassword()).isEqualTo(HASH_B);
		assertThat(result.getPasswordLastChangedAt()).isNotNull();
		verify(repository).save(cred);
	}

	@Test
	void shouldThrowWhenCredentialNotFound() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
		var cmd = new UpdateCredentialsCommand(
				PUBLIC_ID, Optional.of("a@b.com"), Optional.empty(), Optional.empty(), Optional.empty());

		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.update(cmd));
		assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_NOT_FOUND);
	}

	@Test
	void invalidPublicId_throwsFromValidator() {
		var cmd = new UpdateCredentialsCommand("bad", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

		assertThrows(BusinessError.class, () -> useCase.update(cmd));
	}

	@Test
	void saveFailure_propagates() {
		var cred = existing();
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(cred));
		when(repository.save(any(Credential.class))).thenThrow(new RuntimeException("DB error"));

		var cmd = new UpdateCredentialsCommand(
				PUBLIC_ID, Optional.of("x@y.com"), Optional.empty(), Optional.empty(), Optional.empty());

		assertThrows(RuntimeException.class, () -> useCase.update(cmd));
	}
}
