package com.auth.units.service.sessions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByRefreshTokenQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByUserIdQuery;
import com.auth.domain.services.validators.sessions.GetSessionsQueriesValidators;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("GetSessionsQueriesValidators (full coverage)")
class GetSessionsQueriesServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";
	private static final UUID VALID_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Nested
	@DisplayName("FindByPublicIdQuery")
	class FindByPublicId {

		static Stream<Arguments> invalidPublicIds() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"));
		}

		@ParameterizedTest(name = "should throw SESSIONS_PUBLIC_ID_INVALID")
		@MethodSource("invalidPublicIds")
		void invalid(String publicId) {
			var q = new FindByPublicIdQuery(publicId);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetSessionsQueriesValidators.validate(q));
			assertEquals(CodesError.SESSIONS_PUBLIC_ID_INVALID, ex.getCode());
		}

		@Test
		void valid() {
			assertDoesNotThrow(() -> GetSessionsQueriesValidators.validate(new FindByPublicIdQuery(VALID_NANOID)));
		}
	}

	@Nested
	@DisplayName("FindByUserIdQuery")
	class FindByUserId {

		@Test
		void invalidNullUserId() {
			var q = new FindByUserIdQuery(null);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetSessionsQueriesValidators.validate(q));
			assertEquals(CodesError.SESSIONS_USER_ID_INVALID, ex.getCode());
		}

		@Test
		void valid() {
			assertDoesNotThrow(() -> GetSessionsQueriesValidators.validate(new FindByUserIdQuery(VALID_USER_ID)));
		}
	}

	@Nested
	@DisplayName("FindByRefreshTokenQuery")
	class FindByRefreshToken {

		static Stream<Arguments> blanks() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("  "));
		}

		@ParameterizedTest(name = "should throw SESSIONS_REFRESH_TOKEN_INVALID")
		@MethodSource("blanks")
		void invalid(String token) {
			var q = new FindByRefreshTokenQuery(token);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetSessionsQueriesValidators.validate(q));
			assertEquals(CodesError.SESSIONS_REFRESH_TOKEN_INVALID, ex.getCode());
		}

		@Test
		void valid() {
			assertDoesNotThrow(() -> GetSessionsQueriesValidators.validate(new FindByRefreshTokenQuery("opaque-token")));
		}
	}
}
