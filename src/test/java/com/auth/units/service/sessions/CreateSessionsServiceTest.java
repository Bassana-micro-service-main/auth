package com.auth.units.service.sessions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort.CreateSessionsCommand;
import com.auth.domain.services.validators.sessions.CreateSessionsValidators;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("CreateSessionsValidators (full coverage)")
class CreateSessionsServiceTest {

	private static final UUID VALID_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
	private static final Instant FUTURE = Instant.now().plus(7, ChronoUnit.DAYS);

	private static CreateSessionsCommand validCommand() {
		return new CreateSessionsCommand(
				VALID_USER_ID,
				"192.168.1.1",
				"Mozilla/5.0",
				"Chrome",
				"opaque-refresh-token-value",
				FUTURE);
	}

	@Test
	@DisplayName("should pass validation for a completely valid command")
	void shouldPassCompletelyValid() {
		assertDoesNotThrow(() -> CreateSessionsValidators.validate(validCommand()));
	}

	@Nested
	@DisplayName("invalid cases")
	class InvalidCases {

		@Test
		@DisplayName("should throw SESSIONS_USER_ID_INVALID when userId is null")
		void invalidUserId() {
			var cmd = new CreateSessionsCommand(null, "1.1.1.1", "ua", "dev", "rt", FUTURE);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_USER_ID_INVALID, ex.getCode());
		}

		static Stream<Arguments> blankStrings() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("   "));
		}

		@ParameterizedTest(name = "should throw SESSIONS_IP_ADDRESS_INVALID when ipAddress is blank: \"{0}\"")
		@MethodSource("blankStrings")
		void invalidIp(String ip) {
			var cmd = new CreateSessionsCommand(VALID_USER_ID, ip, "ua", "dev", "rt", FUTURE);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_IP_ADDRESS_INVALID, ex.getCode());
		}

		@ParameterizedTest(name = "should throw SESSIONS_USER_AGENT_INVALID when userAgent is blank: \"{0}\"")
		@MethodSource("blankStrings")
		void invalidUserAgent(String ua) {
			var cmd = new CreateSessionsCommand(VALID_USER_ID, "1.1.1.1", ua, "dev", "rt", FUTURE);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_USER_AGENT_INVALID, ex.getCode());
		}

		@ParameterizedTest(name = "should throw SESSIONS_DEVICE_NAME_INVALID when deviceName is blank: \"{0}\"")
		@MethodSource("blankStrings")
		void invalidDeviceName(String name) {
			var cmd = new CreateSessionsCommand(VALID_USER_ID, "1.1.1.1", "ua", name, "rt", FUTURE);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_DEVICE_NAME_INVALID, ex.getCode());
		}

		@ParameterizedTest(name = "should throw SESSIONS_REFRESH_TOKEN_INVALID when refreshToken is blank: \"{0}\"")
		@MethodSource("blankStrings")
		void invalidRefreshToken(String rt) {
			var cmd = new CreateSessionsCommand(VALID_USER_ID, "1.1.1.1", "ua", "dev", rt, FUTURE);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_REFRESH_TOKEN_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw SESSIONS_EXPIRES_AT_INVALID when expiresAt is null")
		void invalidExpiresAtNull() {
			var cmd = new CreateSessionsCommand(VALID_USER_ID, "1.1.1.1", "ua", "dev", "rt", null);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_EXPIRES_AT_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw SESSIONS_EXPIRES_AT_INVALID when expiresAt is in the past")
		void invalidExpiresAtPast() {
			var cmd = new CreateSessionsCommand(
					VALID_USER_ID, "1.1.1.1", "ua", "dev", "rt", Instant.now().minus(1, ChronoUnit.HOURS));
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateSessionsValidators.validate(cmd));
			assertEquals(CodesError.SESSIONS_EXPIRES_AT_INVALID, ex.getCode());
		}
	}

	@Nested
	@DisplayName("valid field variations")
	class ValidVariations {

		static Stream<Arguments> validIps() {
			return Stream.of(Arguments.of("10.0.0.1"), Arguments.of("2001:db8::1"));
		}

		@ParameterizedTest(name = "should NOT throw when ipAddress is \"{0}\"")
		@MethodSource("validIps")
		void validIp(String ip) {
			var cmd = new CreateSessionsCommand(VALID_USER_ID, ip, "ua", "dev", "rt", FUTURE);
			assertDoesNotThrow(() -> CreateSessionsValidators.validate(cmd));
		}

		static Stream<Arguments> validUserIds() {
			return Stream.of(
					Arguments.of(UUID.fromString("00000000-0000-4000-8000-000000000001")),
					Arguments.of(UUID.fromString("ffffffff-ffff-4fff-bfff-ffffffffffff")));
		}

		@ParameterizedTest(name = "should NOT throw when userId is {0}")
		@MethodSource("validUserIds")
		void validUserId(UUID userId) {
			var cmd = new CreateSessionsCommand(userId, "1.1.1.1", "ua", "dev", "rt", FUTURE);
			assertDoesNotThrow(() -> CreateSessionsValidators.validate(cmd));
		}
	}
}
