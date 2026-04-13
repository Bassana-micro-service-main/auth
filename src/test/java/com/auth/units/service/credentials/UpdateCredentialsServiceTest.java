package com.auth.units.service.credentials;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort.UpdateCredentialsCommand;
import com.auth.domain.services.validators.credentials.UpdateCredentialsValidators;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("UpdateCredentialsValidators (full coverage)")
class UpdateCredentialsServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";

	private static final String VALID_BCRYPT_A =
			"$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
	private static final String VALID_BCRYPT_B =
			"$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

	private static UpdateCredentialsCommand minimalValid() {
		return new UpdateCredentialsCommand(
				VALID_NANOID, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	@Test
	@DisplayName("should pass validation for minimal valid command (publicId only)")
	void shouldPassMinimalValid() {
		assertDoesNotThrow(() -> UpdateCredentialsValidators.validate(minimalValid()));
	}

	@Nested
	@DisplayName("invalid cases")
	class InvalidCases {

		static Stream<Arguments> invalidPublicIds() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("bad"), Arguments.of("a".repeat(22)));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_PUBLIC_ID_INVALID when publicId is \"{0}\"")
		@MethodSource("invalidPublicIds")
		void invalidPublicId(String publicId) {
			var cmd = new UpdateCredentialsCommand(
					publicId, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateCredentialsValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_PUBLIC_ID_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidEmailsWhenPresent() {
			return Stream.of(Arguments.of("bademail"), Arguments.of("a@b"), Arguments.of(""));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_EMAIL_INVALID when email is present and invalid: \"{0}\"")
		@MethodSource("invalidEmailsWhenPresent")
		void invalidEmailWhenPresent(String email) {
			var cmd = new UpdateCredentialsCommand(
					VALID_NANOID, Optional.of(email), Optional.empty(), Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateCredentialsValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_EMAIL_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidHashesWhenPresent() {
			return Stream.of(Arguments.of("short"), Arguments.of("not-a-bcrypt-hash"));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_PASSWORD_INVALID when hashedPassword is invalid")
		@MethodSource("invalidHashesWhenPresent")
		void invalidHashedPasswordWhenPresent(String hash) {
			var cmd = new UpdateCredentialsCommand(
					VALID_NANOID, Optional.empty(), Optional.of(hash), Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateCredentialsValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_PASSWORD_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidSaltWhenPresent() {
			return Stream.of(Arguments.of(""), Arguments.of("   "));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_PASSWORD_INVALID when passwordSalt is blank")
		@MethodSource("invalidSaltWhenPresent")
		void invalidPasswordSaltWhenPresent(String salt) {
			var cmd = new UpdateCredentialsCommand(
					VALID_NANOID, Optional.empty(), Optional.empty(), Optional.of(salt), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateCredentialsValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_PASSWORD_INVALID, ex.getCode());
		}
	}

	@Nested
	@DisplayName("valid field variations")
	class ValidVariations {

		static Stream<Arguments> validEmails() {
			return Stream.of(Arguments.of("a@b.com"), Arguments.of("user+tag@example.org"));
		}

		@ParameterizedTest(name = "should NOT throw when email is \"{0}\"")
		@MethodSource("validEmails")
		void validEmail(String email) {
			var cmd = new UpdateCredentialsCommand(
					VALID_NANOID, Optional.of(email), Optional.empty(), Optional.empty(), Optional.empty());
			assertDoesNotThrow(() -> UpdateCredentialsValidators.validate(cmd));
		}

		static Stream<Arguments> validHashes() {
			return Stream.of(Arguments.of(VALID_BCRYPT_A), Arguments.of(VALID_BCRYPT_B));
		}

		@ParameterizedTest(name = "should NOT throw when hashedPassword is valid bcrypt")
		@MethodSource("validHashes")
		void validHash(String hash) {
			var cmd = new UpdateCredentialsCommand(
					VALID_NANOID, Optional.empty(), Optional.of(hash), Optional.empty(), Optional.empty());
			assertDoesNotThrow(() -> UpdateCredentialsValidators.validate(cmd));
		}

		static Stream<Arguments> validSalts() {
			return Stream.of(Arguments.of("salt-value"), Arguments.of("x"));
		}

		@ParameterizedTest(name = "should NOT throw when passwordSalt is non-blank")
		@MethodSource("validSalts")
		void validSalt(String salt) {
			var cmd = new UpdateCredentialsCommand(
					VALID_NANOID, Optional.empty(), Optional.empty(), Optional.of(salt), Optional.empty());
			assertDoesNotThrow(() -> UpdateCredentialsValidators.validate(cmd));
		}
	}
}
