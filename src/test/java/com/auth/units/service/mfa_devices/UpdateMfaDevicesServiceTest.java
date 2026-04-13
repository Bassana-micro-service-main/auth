package com.auth.units.service.mfa_devices;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort.UpdateMfaDevicesCommand;
import com.auth.domain.services.validators.mfa_devices.UpdateMfaDevicesValidators;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("UpdateMfaDevicesValidators (full coverage)")
class UpdateMfaDevicesServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";

	private static UpdateMfaDevicesCommand minimalValidCommand() {
		return new UpdateMfaDevicesCommand(
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
		assertDoesNotThrow(() -> UpdateMfaDevicesValidators.validate(minimalValidCommand()));
	}

	@Nested
	@DisplayName("invalid cases")
	class InvalidCases {

		static Stream<Arguments> invalidPublicIds() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"));
		}

		@ParameterizedTest(name = "should throw MFA_DEVICES_PUBLIC_ID_INVALID")
		@MethodSource("invalidPublicIds")
		void invalidPublicId(String publicId) {
			var cmd = new UpdateMfaDevicesCommand(
					publicId,
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_PUBLIC_ID_INVALID, ex.getCode());
		}

		static Stream<Arguments> blankSecretOptionals() {
			return Stream.of(Arguments.of(Optional.of("")), Arguments.of(Optional.of("  ")));
		}

		@ParameterizedTest(name = "should throw MFA_DEVICES_SECRET_INVALID when secret optional is blank")
		@MethodSource("blankSecretOptionals")
		void invalidSecret(Optional<String> secret) {
			var cmd = new UpdateMfaDevicesCommand(
					VALID_NANOID, Optional.empty(), secret, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_SECRET_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw MFA_DEVICES_PHONE_INVALID when phoneNumber optional is blank")
		void invalidPhoneBlank() {
			var cmd = new UpdateMfaDevicesCommand(
					VALID_NANOID,
					Optional.empty(),
					Optional.empty(),
					Optional.of(""),
					Optional.empty(),
					Optional.empty(),
					Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_PHONE_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw MFA_DEVICES_PHONE_INVALID when phoneNumber is neither valid email nor phone")
		void invalidPhoneFormat() {
			var cmd = new UpdateMfaDevicesCommand(
					VALID_NANOID,
					Optional.empty(),
					Optional.empty(),
					Optional.of("nope"),
					Optional.empty(),
					Optional.empty(),
					Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_PHONE_INVALID, ex.getCode());
		}

		static Stream<Arguments> blankDeviceOptionals() {
			return Stream.of(Arguments.of(Optional.of("")), Arguments.of(Optional.of("  ")));
		}

		@ParameterizedTest(name = "should throw MFA_DEVICES_DEVICE_NAME_INVALID when deviceName optional is blank")
		@MethodSource("blankDeviceOptionals")
		void invalidDeviceName(Optional<String> name) {
			var cmd = new UpdateMfaDevicesCommand(
					VALID_NANOID, Optional.empty(), Optional.empty(), Optional.empty(), name, Optional.empty(), Optional.empty());
			BusinessError ex = assertThrows(BusinessError.class, () -> UpdateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_DEVICE_NAME_INVALID, ex.getCode());
		}
	}

	@Nested
	@DisplayName("valid field variations")
	class ValidVariations {

		@Test
		void validPhoneUpdate() {
			var cmd = new UpdateMfaDevicesCommand(
					VALID_NANOID,
					Optional.empty(),
					Optional.empty(),
					Optional.of("+12345678901"),
					Optional.empty(),
					Optional.empty(),
					Optional.empty());
			assertDoesNotThrow(() -> UpdateMfaDevicesValidators.validate(cmd));
		}

		@Test
		void validEmailUpdate() {
			var cmd = new UpdateMfaDevicesCommand(
					VALID_NANOID,
					Optional.empty(),
					Optional.empty(),
					Optional.of("user@example.com"),
					Optional.empty(),
					Optional.empty(),
					Optional.empty());
			assertDoesNotThrow(() -> UpdateMfaDevicesValidators.validate(cmd));
		}

		@Test
		void validSecretUpdate() {
			var cmd = new UpdateMfaDevicesCommand(
					VALID_NANOID,
					Optional.empty(),
					Optional.of("new-secret"),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty());
			assertDoesNotThrow(() -> UpdateMfaDevicesValidators.validate(cmd));
		}

		@Test
		void validDeviceNameUpdate() {
			var cmd = new UpdateMfaDevicesCommand(
					VALID_NANOID,
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.of("Pixel"),
					Optional.empty(),
					Optional.empty());
			assertDoesNotThrow(() -> UpdateMfaDevicesValidators.validate(cmd));
		}
	}
}
