package com.auth.units.service.sessions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort.UpdateSessionsCommand;
import com.auth.domain.services.validators.sessions.UpdateSessionsValidators;
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

@DisplayName("UpdateSessionsValidators (full coverage)")
class UpdateSessionsServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";
	private static final Instant FUTURE = Instant.now().plus(7, ChronoUnit.DAYS);

	private static UpdateSessionsCommand minimalValidCommand() {
		return new UpdateSessionsCommand(
				VALID_NANOID,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty());
	}

	@Test
	@DisplayName("should pass for minimal valid command")
	void minimalValid() {
		assertDoesNotThrow(() -> UpdateSessionsValidators.validate(minimalValidCommand()));
	}

	@Nested
	@DisplayName("invalid cases")
	class InvalidCases {

		static Stream<Arguments> invalidPublicIds() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"));
		}

		@ParameterizedTest(name = "should throw SESSIONS_PUBLIC_ID_INVALID when publicId is invalid")
		@MethodSource("invalidPublicIds")
		void invalidPublicId(String publicId) {
			var cmd = new UpdateSessionsCommand(
					publicId,
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_PUBLIC_ID_INVALID, ex.getCode());
		}

		static Stream<Arguments> blankOptionals() {
			return Stream.of(Arguments.of(Optional.of("")), Arguments.of(Optional.of("   ")));
		}

		@ParameterizedTest(name = "should throw SESSIONS_IP_ADDRESS_INVALID when ipAddress optional is blank")
		@MethodSource("blankOptionals")
		void invalidIpWhenPresent(Optional<String> ip) {
			var cmd = new UpdateSessionsCommand(
					VALID_NANOID, ip, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_IP_ADDRESS_INVALID, ex.getCode());
		}

		@ParameterizedTest(name = "should throw SESSIONS_USER_AGENT_INVALID when userAgent optional is blank")
		@MethodSource("blankOptionals")
		void invalidUaWhenPresent(Optional<String> ua) {
			var cmd = new UpdateSessionsCommand(
					VALID_NANOID, Optional.empty(), ua, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_USER_AGENT_INVALID, ex.getCode());
		}

		@ParameterizedTest(name = "should throw SESSIONS_DEVICE_NAME_INVALID when deviceName optional is blank")
		@MethodSource("blankOptionals")
		void invalidDeviceWhenPresent(Optional<String> d) {
			var cmd = new UpdateSessionsCommand(
					VALID_NANOID, Optional.empty(), Optional.empty(), d, Optional.empty(), Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_DEVICE_NAME_INVALID, ex.getCode());
		}

		@ParameterizedTest(name = "should throw SESSIONS_REFRESH_TOKEN_INVALID when refreshToken optional is blank")
		@MethodSource("blankOptionals")
		void invalidRtWhenPresent(Optional<String> rt) {
			var cmd = new UpdateSessionsCommand(
					VALID_NANOID, Optional.empty(), Optional.empty(), Optional.empty(), rt, Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_REFRESH_TOKEN_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw SESSIONS_EXPIRES_AT_INVALID when expiresAt is present but not in the future")
		void invalidExpiresAtPast() {
			var past = Instant.now().minus(1, ChronoUnit.HOURS);
			var cmd = new UpdateSessionsCommand(
					VALID_NANOID,
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.of(past),
					Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_EXPIRES_AT_INVALID, ex.getCode());
		}
	}

	@Nested
	@DisplayName("valid field variations")
	class ValidVariations {

		@Test
		@DisplayName("should NOT throw when expiresAt is in the future")
		void validExpiresAt() {
			var cmd = new UpdateSessionsCommand(
					VALID_NANOID,
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.of(FUTURE),
					Optional.empty());
			assertDoesNotThrow(() -> UpdateSessionsValidators.validate(cmd));
		}

		@Test
		@DisplayName("should NOT throw when optional strings are non-blank")
		void validOptionals() {
			var cmd = new UpdateSessionsCommand(
					VALID_NANOID,
					Optional.of("10.0.0.2"),
					Optional.of("Safari"),
					Optional.of("Phone"),
					Optional.of("new-refresh"),
					Optional.empty(),
					Optional.empty());
			assertDoesNotThrow(() -> UpdateSessionsValidators.validate(cmd));
		}
	}
}
