package com.auth.units.service.auth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.LoginInterfacePort.LoginCommand;
import com.auth.domain.ports.in.auth.LogoutInterfacePort.LogoutCommand;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort.RefreshTokenCommand;
import com.auth.domain.ports.in.auth.RegisterInterfacePort.RegisterCommand;
import com.auth.domain.services.validators.auth.AuthenticationValidators;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("AuthenticationValidators (full coverage)")
class AuthenticationValidatorsServiceTest {

	@Nested
	@DisplayName("LoginCommand")
	class Login {

		private static LoginCommand valid() {
			return new LoginCommand("user@example.com", "secretpass", "1.1.1.1", "ua", "device");
		}

		@Test
		void validCommand() {
			assertDoesNotThrow(() -> AuthenticationValidators.validate(valid()));
		}

		static Stream<Arguments> invalidEmails() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("bad"), Arguments.of("a@b"));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_EMAIL_INVALID when email is invalid")
		@MethodSource("invalidEmails")
		void invalidEmail(String email) {
			var cmd = new LoginCommand(email, "password", "1.1.1.1", "ua", "d");
			BusinessError ex = assertThrows(BusinessError.class, () -> AuthenticationValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_EMAIL_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidPasswords() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("   "));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_PASSWORD_INVALID when plainPassword is blank")
		@MethodSource("invalidPasswords")
		void invalidPassword(String pwd) {
			var cmd = new LoginCommand("user@example.com", pwd, "1.1.1.1", "ua", "d");
			BusinessError ex = assertThrows(BusinessError.class, () -> AuthenticationValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_PASSWORD_INVALID, ex.getCode());
		}

		static Stream<Arguments> validEmails() {
			return Stream.of(Arguments.of("a@b.co"), Arguments.of("alias@domain.example"));
		}

		@ParameterizedTest(name = "should NOT throw for valid email \"{0}\"")
		@MethodSource("validEmails")
		void validEmailsOnly(String email) {
			var cmd = new LoginCommand(email, "x", "ip", "ua", "d");
			assertDoesNotThrow(() -> AuthenticationValidators.validate(cmd));
		}
	}

	@Nested
	@DisplayName("RegisterCommand")
	class Register {

		private static RegisterCommand valid() {
			return new RegisterCommand("new@example.com", "Aa123456!", "1.1.1.1", "ua", "device");
		}

		@Test
		void validCommand() {
			assertDoesNotThrow(() -> AuthenticationValidators.validate(valid()));
		}

		static Stream<Arguments> invalidEmails() {
			return Stream.of(Arguments.of((String) null), Arguments.of("x@y"));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_EMAIL_INVALID")
		@MethodSource("invalidEmails")
		void invalidEmail(String email) {
			var cmd = new RegisterCommand(email, "Aa123456!", "1.1.1.1", "ua", "d");
			BusinessError ex = assertThrows(BusinessError.class, () -> AuthenticationValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_EMAIL_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidPasswords() {
			return Stream.of(
					Arguments.of((String) null),
					Arguments.of("short"),
					Arguments.of("12345678"),
					Arguments.of("Password1"),
					Arguments.of("Passw0rd"));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_PASSWORD_INVALID when password does not match policy")
		@MethodSource("invalidPasswords")
		void invalidPassword(String pwd) {
			var cmd = new RegisterCommand("user@example.com", pwd, "1.1.1.1", "ua", "d");
			BusinessError ex = assertThrows(BusinessError.class, () -> AuthenticationValidators.validate(cmd));
			assertEquals(CodesError.CREDENTIALS_PASSWORD_INVALID, ex.getCode());
		}

		static Stream<Arguments> validPasswords() {
			return Stream.of(Arguments.of("Aa123456!"), Arguments.of("Str0ng#pwd"), Arguments.of("x1!abcdefgh"));
		}

		@ParameterizedTest(name = "should NOT throw for valid password policy")
		@MethodSource("validPasswords")
		void validPasswords(String pwd) {
			var cmd = new RegisterCommand("reg@example.com", pwd, "1.1.1.1", "ua", "d");
			assertDoesNotThrow(() -> AuthenticationValidators.validate(cmd));
		}
	}

	@Nested
	@DisplayName("LogoutCommand")
	class Logout {

		@Test
		void valid() {
			assertDoesNotThrow(() -> AuthenticationValidators.validate(new LogoutCommand("refresh-token")));
		}

		static Stream<Arguments> blanks() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("  "));
		}

		@ParameterizedTest(name = "should throw SESSIONS_REFRESH_TOKEN_INVALID")
		@MethodSource("blanks")
		void invalid(String token) {
			var cmd = new LogoutCommand(token);
			BusinessError ex = assertThrows(BusinessError.class, () -> AuthenticationValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_REFRESH_TOKEN_INVALID, ex.getCode());
		}
	}

	@Nested
	@DisplayName("RefreshTokenCommand")
	class Refresh {

		@Test
		void valid() {
			assertDoesNotThrow(() -> AuthenticationValidators.validate(new RefreshTokenCommand("refresh-token")));
		}

		static Stream<Arguments> blanks() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("  "));
		}

		@ParameterizedTest(name = "should throw SESSIONS_REFRESH_TOKEN_INVALID")
		@MethodSource("blanks")
		void invalid(String token) {
			var cmd = new RefreshTokenCommand(token);
			BusinessError ex = assertThrows(BusinessError.class, () -> AuthenticationValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_REFRESH_TOKEN_INVALID, ex.getCode());
		}
	}
}
