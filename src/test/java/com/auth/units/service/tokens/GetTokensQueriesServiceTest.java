package com.auth.units.service.tokens;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.enums.TokenType;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByTypeQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByValueQuery;
import com.auth.domain.services.validators.tokens.GetTokensQueriesValidators;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("GetTokensQueriesValidators (full coverage)")
class GetTokensQueriesServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";

	@Nested
	@DisplayName("FindByPublicIdQuery")
	class FindByPublicId {

		static Stream<Arguments> invalidPublicIds() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"));
		}

		@ParameterizedTest(name = "should throw TOKENS_PUBLIC_ID_INVALID")
		@MethodSource("invalidPublicIds")
		void invalid(String publicId) {
			var q = new FindByPublicIdQuery(publicId);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetTokensQueriesValidators.validate(q));
			assertEquals(CodesError.TOKENS_PUBLIC_ID_INVALID, ex.getCode());
		}

		@Test
		void valid() {
			assertDoesNotThrow(() -> GetTokensQueriesValidators.validate(new FindByPublicIdQuery(VALID_NANOID)));
		}
	}

	@Nested
	@DisplayName("FindByTypeQuery")
	class FindByType {

		@Test
		void invalidNullType() {
			var q = new FindByTypeQuery(null);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetTokensQueriesValidators.validate(q));
			assertEquals(CodesError.TOKENS_TYPE_INVALID, ex.getCode());
		}

		@Test
		void valid() {
			assertDoesNotThrow(() -> GetTokensQueriesValidators.validate(new FindByTypeQuery(TokenType.ACCESS)));
		}
	}

	@Nested
	@DisplayName("FindByValueQuery")
	class FindByValue {

		static Stream<Arguments> blanks() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("  "));
		}

		@ParameterizedTest(name = "should throw TOKENS_VALUE_INVALID")
		@MethodSource("blanks")
		void invalid(String value) {
			var q = new FindByValueQuery(value);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetTokensQueriesValidators.validate(q));
			assertEquals(CodesError.TOKENS_VALUE_INVALID, ex.getCode());
		}

		@Test
		void valid() {
			assertDoesNotThrow(() -> GetTokensQueriesValidators.validate(new FindByValueQuery("lookup-by-value")));
		}
	}
}
