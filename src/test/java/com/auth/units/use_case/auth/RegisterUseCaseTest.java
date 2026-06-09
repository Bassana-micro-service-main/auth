package com.auth.units.use_case.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.auth.AuthSessionIssuer;
import com.auth.application.use_case.auth.RegisterUseCase;
import com.auth.domain.entities.Credential;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.AuthSessionResult;
import com.auth.domain.ports.in.auth.RegisterInterfacePort.RegisterCommand;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort.CreateCredentialsCommand;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.domain.ports.out.PasswordCryptoPort;
import com.auth.domain.ports.out.UserProfileClientPort;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

	private static final UUID NEW_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Mock
	private CredentialsRepositoryPort credentials;

	@Mock
	private CreateCredentialsInterfacePort createCredentials;

	@Mock
	private PasswordCryptoPort passwordCrypto;

	@Mock
	private UserProfileClientPort userProfile;

	@Mock
	private AuthSessionIssuer authSessionIssuer;

	private RegisterUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new RegisterUseCase(credentials, createCredentials, passwordCrypto, userProfile, authSessionIssuer);
	}

	private RegisterCommand command() {
		return new RegisterCommand("new@test.com", "Aa123456!", "10.0.0.1", "Mozilla", "Device");
	}

	@Test
	void shouldCreateUserProfileCredentialsAndIssueSession() {
		when(credentials.findByEmail("new@test.com")).thenReturn(Optional.empty());
		when(userProfile.createUser("new@test.com")).thenReturn(NEW_USER_ID);
		when(passwordCrypto.hash("Aa123456!")).thenReturn("bcrypt-hash");
		when(createCredentials.create(any(CreateCredentialsCommand.class))).thenReturn(new Credential());
		var sessionResult =
				new AuthSessionResult("at", "rt", Instant.now(), Instant.now().plusSeconds(3600), "abcdefghij12345678901");
		when(authSessionIssuer.issueSessionAndTokens(
						eq(NEW_USER_ID), eq("10.0.0.1"), eq("Mozilla"), eq("Device")))
				.thenReturn(sessionResult);

		AuthSessionResult result = useCase.register(command());

		assertThat(result).isEqualTo(sessionResult);

		ArgumentCaptor<CreateCredentialsCommand> captor = ArgumentCaptor.forClass(CreateCredentialsCommand.class);
		verify(createCredentials).create(captor.capture());
		assertThat(captor.getValue().userId()).isEqualTo(NEW_USER_ID);
		assertThat(captor.getValue().email()).isEqualTo("new@test.com");
		assertThat(captor.getValue().hashedPassword()).isEqualTo("bcrypt-hash");
	}

	@Test
	void emailAlreadyRegistered() {
		when(credentials.findByEmail("new@test.com")).thenReturn(Optional.of(new Credential()));
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.register(command()));
		assertThat(ex.getCode()).isEqualTo(CodesError.AUTH_EMAIL_ALREADY_REGISTERED);
	}
}
