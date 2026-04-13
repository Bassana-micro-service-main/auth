package com.auth.units.use_case.credentials;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.credentials.CreateCredentialsUseCase;
import com.auth.domain.entities.Credential;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort.CreateCredentialsCommand;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.lib.Utils;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests unitaires de {@link CreateCredentialsUseCase} (équivalent
 * {@code create-user.usecase.full.spec.ts} : mock du dépôt, chemins succès / erreurs).
 * <p>
 * Le use case Java ne duplique pas l’email, ne hash pas le mot de passe ni n’injecte un
 * générateur d’id : validation + {@link com.auth.lib.Utils#newNanoid()} + {@code save}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCredentialsUseCase (full coverage)")
class CreateCredentialUseCaseTest {

	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	private static final String VALID_BCRYPT =
			"$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

	@Mock
	private CredentialsRepositoryPort repository;

	private CreateCredentialsUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new CreateCredentialsUseCase(repository);
	}

	private static CreateCredentialsCommand validCommand() {
		return new CreateCredentialsCommand(USER_ID, "user@example.com", VALID_BCRYPT, null);
	}

	@Test
	@DisplayName("should validate, assign publicId (nanoid), set fields and save credential")
	void shouldValidateGeneratePublicIdAndSave() {
		var cmd = validCommand();
		when(repository.save(any(Credential.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Credential result = useCase.create(cmd);

		ArgumentCaptor<Credential> captor = ArgumentCaptor.forClass(Credential.class);
		verify(repository).save(captor.capture());
		Credential saved = captor.getValue();

		assertThat(saved.getPublicId()).isNotNull().hasSize(21).matches(Utils.NANOID_REGEX.pattern());
		assertThat(saved.getUserId()).isEqualTo(cmd.userId());
		assertThat(saved.getEmail()).isEqualTo(cmd.email());
		assertThat(saved.getHashedPassword()).isEqualTo(cmd.hashedPassword());
		assertThat(saved.getPasswordSalt()).isNull();
		assertThat(saved.isActive()).isTrue();
		assertThat(saved.getPasswordLastChangedAt()).isNotNull();

		assertThat(result).isSameAs(saved);
	}

	@Test
	@DisplayName("should propagate exception when repository.save fails")
	void shouldPropagateWhenSaveFails() {
		var cmd = validCommand();
		when(repository.save(any(Credential.class))).thenThrow(new RuntimeException("DB save failed"));

		assertThatThrownBy(() -> useCase.create(cmd))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("DB save failed");
	}

	@Nested
	@DisplayName("validation failures (BusinessError, save never called)")
	class ValidationFailures {

		@Test
		void shouldRejectNullUserId() {
			var cmd = new CreateCredentialsCommand(null, "user@example.com", VALID_BCRYPT, null);

			BusinessError ex = assertThrows(BusinessError.class, () -> useCase.create(cmd));
			assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_USER_ID_INVALID);

			verify(repository, never()).save(any());
		}

		static Stream<Arguments> invalidEmails() {
			return Stream.of(
					Arguments.of((String) null),
					Arguments.of(""),
					Arguments.of("bademail"),
					Arguments.of("a@b"),
					Arguments.of("user@"));
		}

		@ParameterizedTest(name = "invalid email: {0}")
		@MethodSource("invalidEmails")
		void shouldRejectInvalidEmail(String email) {
			var cmd = new CreateCredentialsCommand(USER_ID, email, VALID_BCRYPT, null);

			BusinessError ex = assertThrows(BusinessError.class, () -> useCase.create(cmd));
			assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_EMAIL_INVALID);

			verify(repository, never()).save(any());
		}

		static Stream<Arguments> invalidHashes() {
			return Stream.of(
					Arguments.of((String) null),
					Arguments.of("short"),
					Arguments.of("not-a-bcrypt"));
		}

		@ParameterizedTest(name = "invalid hashedPassword: {0}")
		@MethodSource("invalidHashes")
		void shouldRejectInvalidHashedPassword(String hash) {
			var cmd = new CreateCredentialsCommand(USER_ID, "user@example.com", hash, null);

			BusinessError ex = assertThrows(BusinessError.class, () -> useCase.create(cmd));
			assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_PASSWORD_INVALID);

			verify(repository, never()).save(any());
		}

		@Test
		void shouldRejectBlankPasswordSaltWhenPresent() {
			var cmd = new CreateCredentialsCommand(USER_ID, "user@example.com", VALID_BCRYPT, "   ");

			BusinessError ex = assertThrows(BusinessError.class, () -> useCase.create(cmd));
			assertThat(ex.getCode()).isEqualTo(CodesError.CREDENTIALS_PASSWORD_INVALID);

			verify(repository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("valid field variations (happy path)")
	class ValidVariations {

		static Stream<Arguments> validEmails() {
			return Stream.of(Arguments.of("a@b.co"), Arguments.of("alias+tag@domain.example"));
		}

		@ParameterizedTest(name = "email {0}")
		@MethodSource("validEmails")
		void shouldAcceptValidEmails(String email) {
			var cmd = new CreateCredentialsCommand(USER_ID, email, VALID_BCRYPT, null);
			when(repository.save(any(Credential.class))).thenAnswer(invocation -> invocation.getArgument(0));

			assertThat(useCase.create(cmd).getEmail()).isEqualTo(email);
		}

		@Test
		void shouldAcceptOptionalNonBlankPasswordSalt() {
			var cmd = new CreateCredentialsCommand(USER_ID, "user@example.com", VALID_BCRYPT, "salt-value");
			when(repository.save(any(Credential.class))).thenAnswer(invocation -> invocation.getArgument(0));

			Credential result = useCase.create(cmd);

			assertThat(result.getPasswordSalt()).isEqualTo("salt-value");
		}
	}
}
