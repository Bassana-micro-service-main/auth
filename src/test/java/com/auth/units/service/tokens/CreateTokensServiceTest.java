package com.auth.units.service.tokens;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.enums.TokenType;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort.CreateTokensCommand;
import com.auth.domain.services.validators.tokens.CreateTokensValidators;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("CreateTokensValidators (full coverage)")
class CreateTokensServiceTest {

	private static final Instant FUTURE = Instant.now().plus(7, ChronoUnit.DAYS);

	private static CreateTokensCommand validCommand() {
		return new CreateTokensCommand(TokenType.ACCESS, "token-value", FUTURE);
	}

	@Test
	@DisplayName("should pass for completely valid command")
	void valid() {
		assertDoesNotThrow(() -> CreateTokensValidators.validate(validCommand()));
	}

	@Nested
	@DisplayName("invalid cases")
	class InvalidCases {

		@Test
		@DisplayName("should throw TOKENS_TYPE_INVALID when type is null")
		void invalidType() {
			var cmd = new CreateTokensCommand(null, "v", FUTURE);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateTokensValidators.validate(cmd));
			assertEquals(CodesError.TOKENS_TYPE_INVALID, ex.getCode());
		}

		static Stream<Arguments> blankValues() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("  "));
		}

		@ParameterizedTest(name = "should throw TOKENS_VALUE_INVALID when value is blank")
		@MethodSource("blankValues")
		void invalidValue(String value) {
			var cmd = new CreateTokensCommand(TokenType.REFRESH, value, FUTURE);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateTokensValidators.validate(cmd));
			assertEquals(CodesError.TOKENS_VALUE_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw TOKENS_EXPIRES_AT_INVALID when expiresAt is null")
		void invalidExpiresNull() {
			var cmd = new CreateTokensCommand(TokenType.ACCESS, "v", null);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateTokensValidators.validate(cmd));
			assertEquals(CodesError.TOKENS_EXPIRES_AT_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw TOKENS_EXPIRES_AT_INVALID when expiresAt is in the past")
		void invalidExpiresPast() {
			var cmd = new CreateTokensCommand(
					TokenType.ACCESS, "v", Instant.now().minus(1, ChronoUnit.HOURS));
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateTokensValidators.validate(cmd));
			assertEquals(CodesError.TOKENS_EXPIRES_AT_INVALID, ex.getCode());
		}
	}

	@Nested
	@DisplayName("valid field variations")
	class ValidVariations {

		static Stream<Arguments> types() {
			return Stream.of(
					Arguments.of(TokenType.ACCESS),
					Arguments.of(TokenType.REFRESH),
					Arguments.of(TokenType.PASSWORD_RESET),
					Arguments.of(TokenType.EMAIL_VERIFICATION),
					Arguments.of(TokenType.API_KEY));
		}

		@ParameterizedTest(name = "should NOT throw for type {0}")
		@MethodSource("types")
		void validTypes(TokenType type) {
			var cmd = new CreateTokensCommand(type, "opaque", FUTURE);
			assertDoesNotThrow(() -> CreateTokensValidators.validate(cmd));
		}
	}
}
