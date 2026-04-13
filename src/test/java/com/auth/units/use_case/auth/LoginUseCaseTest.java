package com.auth.units.use_case.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.auth.AuthSessionIssuer;
import com.auth.application.use_case.auth.LoginUseCase;
import com.auth.domain.entities.Credential;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.AuthSessionResult;
import com.auth.domain.ports.in.auth.LoginInterfacePort.LoginCommand;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.domain.ports.out.PasswordCryptoPort;
import com.auth.domain.ports.out.UserProfileClientPort;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Mock
	private CredentialsRepositoryPort credentials;

	@Mock
	private PasswordCryptoPort passwordCrypto;

	@Mock
	private UserProfileClientPort userProfile;

	@Mock
	private AuthSessionIssuer authSessionIssuer;

	private LoginUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new LoginUseCase(credentials, passwordCrypto, userProfile, authSessionIssuer);
	}

	private Credential activeCredential() {
		var c = new Credential();
		c.setUserId(USER_ID);
		c.setEmail("user@test.com");
		c.setHashedPassword("stored-hash");
		c.setActive(true);
		return c;
	}

	private LoginCommand command() {
		return new LoginCommand("user@test.com", "plain-pass", "10.0.0.1", "Mozilla", "Phone");
	}

	@Test
	void shouldLoginAndIssueSession() {
		var cred = activeCredential();
		when(credentials.findByEmail("user@test.com")).thenReturn(Optional.of(cred));
		when(passwordCrypto.matches("plain-pass", "stored-hash")).thenReturn(true);
		var sessionResult =
				new AuthSessionResult("at", "rt", Instant.now(), Instant.now().plusSeconds(3600), "abcdefghij12345678901");
		when(authSessionIssuer.issueSessionAndTokens(
						eq(USER_ID), eq("10.0.0.1"), eq("Mozilla"), eq("Phone")))
				.thenReturn(sessionResult);

		AuthSessionResult result = useCase.login(command());

		assertThat(result).isEqualTo(sessionResult);
		verify(userProfile).assertUserActive(USER_ID);
	}

	@Test
	void emailNotFound() {
		when(credentials.findByEmail("user@test.com")).thenReturn(Optional.empty());
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.login(command()));
		assertThat(ex.getCode()).isEqualTo(CodesError.AUTH_INVALID_CREDENTIALS);
	}

	@Test
	void inactiveCredential() {
		var cred = activeCredential();
		cred.setActive(false);
		when(credentials.findByEmail("user@test.com")).thenReturn(Optional.of(cred));
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.login(command()));
		assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_INACTIVE);
	}

	@Test
	void passwordMismatch() {
		when(credentials.findByEmail("user@test.com")).thenReturn(Optional.of(activeCredential()));
		when(passwordCrypto.matches("plain-pass", "stored-hash")).thenReturn(false);
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.login(command()));
		assertThat(ex.getCode()).isEqualTo(CodesError.AUTH_INVALID_CREDENTIALS);
	}

	@Test
	void invalidEmailFromValidator() {
		BusinessError ex = assertThrows(
				BusinessError.class,
				() -> useCase.login(new LoginCommand("bad-email", "p", "ip", "ua", "d")));
		assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_EMAIL_INVALID);
	}
}
