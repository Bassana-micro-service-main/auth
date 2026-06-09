package com.auth.units.service.credentials;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByEmailQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByUserIdQuery;
import com.auth.domain.services.validators.credentials.GetCredentialsQueriesValidators;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("GetCredentialsQueriesValidators (full coverage)")
class GetCredentialsQueriesServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";
	private static final UUID VALID_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Nested
	@DisplayName("FindByPublicIdQuery")
	class FindByPublicId {

		static Stream<Arguments> invalidPublicIds() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"), Arguments.of("a".repeat(22)));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_PUBLIC_ID_INVALID when publicId is \"{0}\"")
		@MethodSource("invalidPublicIds")
		void invalidPublicId(String publicId) {
			var q = new FindByPublicIdQuery(publicId);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetCredentialsQueriesValidators.validate(q));
			assertEquals(CodesError.CREDENTIALS_PUBLIC_ID_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should pass for valid nanoid")
		void validPublicId() {
			assertDoesNotThrow(() -> GetCredentialsQueriesValidators.validate(new FindByPublicIdQuery(VALID_NANOID)));
		}
	}

	@Nested
	@DisplayName("FindByEmailQuery")
	class FindByEmail {

		static Stream<Arguments> invalidEmails() {
			return Stream.of(
					Arguments.of((String) null),
					Arguments.of(""),
					Arguments.of("bademail"),
					Arguments.of("a@b"),
					Arguments.of("user@"));
		}

		@ParameterizedTest(name = "should throw CREDENTIALS_EMAIL_INVALID when email is \"{0}\"")
		@MethodSource("invalidEmails")
		void invalidEmail(String email) {
			var q = new FindByEmailQuery(email);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetCredentialsQueriesValidators.validate(q));
			assertEquals(CodesError.CREDENTIALS_EMAIL_INVALID, ex.getCode());
		}

		static Stream<Arguments> validEmails() {
			return Stream.of(Arguments.of("a@b.com"), Arguments.of("user.name@example.org"), Arguments.of("u+tag@ex.co.uk"));
		}

		@ParameterizedTest(name = "should NOT throw when email is \"{0}\"")
		@MethodSource("validEmails")
		void validEmail(String email) {
			assertDoesNotThrow(() -> GetCredentialsQueriesValidators.validate(new FindByEmailQuery(email)));
		}
	}

	@Nested
	@DisplayName("FindByUserIdQuery")
	class FindByUserId {

		@Test
		@DisplayName("should throw CREDENTIALS_USER_ID_INVALID when userId is null")
		void invalidUserId() {
			var q = new FindByUserIdQuery(null);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetCredentialsQueriesValidators.validate(q));
			assertEquals(CodesError.CREDENTIALS_USER_ID_INVALID, ex.getCode());
		}

		static Stream<Arguments> validUserIds() {
			return Stream.of(
					Arguments.of(UUID.fromString("00000000-0000-4000-8000-000000000001")),
					Arguments.of(VALID_USER_ID));
		}

		@ParameterizedTest(name = "should NOT throw when userId is {0}")
		@MethodSource("validUserIds")
		void validUserId(UUID userId) {
			assertDoesNotThrow(() -> GetCredentialsQueriesValidators.validate(new FindByUserIdQuery(userId)));
		}
	}
}
