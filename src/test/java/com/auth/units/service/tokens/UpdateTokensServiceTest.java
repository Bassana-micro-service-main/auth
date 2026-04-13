package com.auth.units.service.tokens;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort.UpdateTokensCommand;
import com.auth.domain.services.validators.tokens.UpdateTokensValidators;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("UpdateTokensValidators (full coverage)")
class UpdateTokensServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";
	private static final Instant FUTURE = Instant.now().plus(7, ChronoUnit.DAYS);

	private static UpdateTokensCommand minimalValidCommand() {
		return new UpdateTokensCommand(VALID_NANOID, Optional.empty(), Optional.empty());
	}

	@Test
	@DisplayName("should pass for minimal valid command")
	void minimalValid() {
		assertDoesNotThrow(() -> UpdateTokensValidators.validate(minimalValidCommand()));
	}

	@Nested
	@DisplayName("invalid cases")
	class InvalidCases {

		static Stream<Arguments> invalidPublicIds() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"));
		}

		@ParameterizedTest(name = "should throw TOKENS_PUBLIC_ID_INVALID")
		@MethodSource("invalidPublicIds")
		void invalidPublicId(String publicId) {
			var cmd = new UpdateTokensCommand(publicId, Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateTokensValidators.validate(cmd));
			assertEquals(CodesError.TOKENS_PUBLIC_ID_INVALID, ex.getCode());
		}

		static Stream<Arguments> blankValueOptionals() {
			return Stream.of(Arguments.of(Optional.of("")), Arguments.of(Optional.of("  ")));
		}

		@ParameterizedTest(name = "should throw TOKENS_VALUE_INVALID when value optional is blank")
		@MethodSource("blankValueOptionals")
		void invalidValue(Optional<String> value) {
			var cmd = new UpdateTokensCommand(VALID_NANOID, value, Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateTokensValidators.validate(cmd));
			assertEquals(CodesError.TOKENS_VALUE_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw TOKENS_EXPIRES_AT_INVALID when expiresAt is present but not future")
		void invalidExpires() {
			var past = Instant.now().minus(1, ChronoUnit.HOURS);
			var cmd = new UpdateTokensCommand(VALID_NANOID, Optional.empty(), Optional.of(past));
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateTokensValidators.validate(cmd));
			assertEquals(CodesError.TOKENS_EXPIRES_AT_INVALID, ex.getCode());
		}
	}

	@Nested
	@DisplayName("valid field variations")
	class ValidVariations {

		@Test
		void validValue() {
			var cmd = new UpdateTokensCommand(VALID_NANOID, Optional.of("new-value"), Optional.empty());
			assertDoesNotThrow(() -> UpdateTokensValidators.validate(cmd));
		}

		@Test
		void validExpiresAt() {
			var cmd = new UpdateTokensCommand(VALID_NANOID, Optional.empty(), Optional.of(FUTURE));
			assertDoesNotThrow(() -> UpdateTokensValidators.validate(cmd));
		}
	}
}
