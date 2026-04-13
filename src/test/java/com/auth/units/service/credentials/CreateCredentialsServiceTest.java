package com.auth.units.service.credentials;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort.CreateCredentialsCommand;
import com.auth.domain.services.validators.credentials.CreateCredentialsValidators;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Couverture du validateur de création d’identifiants (équivalent
 * {@code create-user.validator.spec.ts} / Vitest).
 */
@DisplayName("CreateCredentialsValidators (full coverage)")
class CreateCredentialsServiceTest {

	private static final UUID VALID_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	/** Empreintes bcrypt valides (53 caractères après le dernier {@code $}). */
	private static final String VALID_BCRYPT_A =
			"$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
	private static final String VALID_BCRYPT_B =
			"$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

	private static CreateCredentialsCommand validCommand() {
		return new CreateCredentialsCommand(VALID_USER_ID, "user@example.com", VALID_BCRYPT_A, null);
	}

	@Test
	@DisplayName("should pass validation for a completely valid command")
	void shouldPassValidationForCompletelyValidCommand() {
		assertDoesNotThrow(() -> CreateCredentialsValidators.validate(validCommand()));
	}

	@Nested
	@DisplayName("invalid cases")
	class InvalidCases {

		static Stream<Arguments> invalidEmailCases() {
			return Stream.of(
					Arguments.of((String) null),
					Arguments.of(""),
					Arguments.of("bademail"),
					Arguments.of("a@b"),
					Arguments.of("user@"));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_EMAIL_INVALID when email is \"{0}\"")
		@MethodSource("invalidEmailCases")
		void invalidEmail(String email) {
			var cmd = new CreateCredentialsCommand(VALID_USER_ID, email, VALID_BCRYPT_A, null);
			BusinessError ex =
					assertThrows(BusinessError.class, () -> CreateCredentialsValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_EMAIL_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw CREDENTIALS_USER_ID_INVALID when userId is null")
		void invalidUserId() {
			var cmd = new CreateCredentialsCommand(null, "user@example.com", VALID_BCRYPT_A, null);
			BusinessError ex =
					assertThrows(BusinessError.class, () -> CreateCredentialsValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_USER_ID_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidHashedPasswordCases() {
			return Stream.of(
					Arguments.of((String) null),
					Arguments.of("short"),
					Arguments.of("123456"),
					Arguments.of("password"),
					Arguments.of("not-a-bcrypt-hash"));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_PASSWORD_INVALID when hashedPassword is \"{0}\"")
		@MethodSource("invalidHashedPasswordCases")
		void invalidHashedPassword(String hashedPassword) {
			var cmd = new CreateCredentialsCommand(VALID_USER_ID, "user@example.com", hashedPassword, null);
			BusinessError ex =
					assertThrows(BusinessError.class, () -> CreateCredentialsValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_PASSWORD_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidPasswordSaltCases() {
			return Stream.of(Arguments.of(""), Arguments.of("   "));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_PASSWORD_INVALID when passwordSalt is blank: \"{0}\"")
		@MethodSource("invalidPasswordSaltCases")
		void invalidPasswordSaltWhenPresent(String passwordSalt) {
			var cmd = new CreateCredentialsCommand(VALID_USER_ID, "user@example.com", VALID_BCRYPT_A, passwordSalt);
			BusinessError ex =
					assertThrows(BusinessError.class, () -> CreateCredentialsValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_PASSWORD_INVALID, ex.getCode());
		}
	}

	@Nested
	@DisplayName("valid field variations")
	class ValidFieldVariations {

		static Stream<Arguments> validEmailCases() {
			return Stream.of(
					Arguments.of("a@b.com"),
					Arguments.of("user.name@example.org"),
					Arguments.of("user+tag@example.co.uk"));
		}

		@ParameterizedTest(name = "should NOT throw when email is \"{0}\"")
		@MethodSource("validEmailCases")
		void validEmails(String email) {
			var cmd = new CreateCredentialsCommand(VALID_USER_ID, email, VALID_BCRYPT_A, null);
			assertDoesNotThrow(() -> CreateCredentialsValidators.validate(cmd));
		}

		static Stream<Arguments> validHashedPasswordCases() {
			return Stream.of(Arguments.of(VALID_BCRYPT_A), Arguments.of(VALID_BCRYPT_B));
		}

		@ParameterizedTest(name = "should NOT throw when hashedPassword is a valid bcrypt hash")
		@MethodSource("validHashedPasswordCases")
		void validHashedPasswords(String hash) {
			var cmd = new CreateCredentialsCommand(VALID_USER_ID, "user@example.com", hash, null);
			assertDoesNotThrow(() -> CreateCredentialsValidators.validate(cmd));
		}

		static Stream<Arguments> validUserIdCases() {
			return Stream.of(
					Arguments.of(UUID.fromString("00000000-0000-4000-8000-000000000001")),
					Arguments.of(UUID.fromString("ffffffff-ffff-4fff-ffff-ffffffffffff")));
		}

		@ParameterizedTest(name = "should NOT throw when userId is {0}")
		@MethodSource("validUserIdCases")
		void validUserIds(UUID userId) {
			var cmd = new CreateCredentialsCommand(userId, "user@example.com", VALID_BCRYPT_A, null);
			assertDoesNotThrow(() -> CreateCredentialsValidators.validate(cmd));
		}

		static Stream<Arguments> validPasswordSaltCases() {
			return Stream.of(Arguments.of((String) null), Arguments.of("optional-salt-value"));
		}

		@ParameterizedTest(name = "should NOT throw when passwordSalt is {0}")
		@MethodSource("validPasswordSaltCases")
		void validPasswordSalt(String passwordSalt) {
			var cmd = new CreateCredentialsCommand(VALID_USER_ID, "user@example.com", VALID_BCRYPT_A, passwordSalt);
			assertDoesNotThrow(() -> CreateCredentialsValidators.validate(cmd));
		}
	}
}
