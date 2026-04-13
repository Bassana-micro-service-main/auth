package com.auth.units.service.mfa_devices;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.enums.MfaType;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort.CreateMfaDevicesCommand;
import com.auth.domain.services.validators.mfa_devices.CreateMfaDevicesValidators;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("CreateMfaDevicesValidators (full coverage)")
class CreateMfaDevicesServiceTest {

	private static final UUID VALID_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Test
	@DisplayName("should pass for valid TOTP command")
	void validTotp() {
		var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.TOTP, "shared-secret", null, "Authenticator", true);
		assertDoesNotThrow(() -> CreateMfaDevicesValidators.validate(cmd));
	}

	@Test
	@DisplayName("should pass for valid SMS command")
	void validSms() {
		var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.SMS, null, "+12345678901", "SMS", true);
		assertDoesNotThrow(() -> CreateMfaDevicesValidators.validate(cmd));
	}

	@Test
	@DisplayName("should pass for valid EMAIL command")
	void validEmail() {
		var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.EMAIL, null, "user@example.com", "Email MFA", true);
		assertDoesNotThrow(() -> CreateMfaDevicesValidators.validate(cmd));
	}

	@Test
	@DisplayName("should pass for valid SECURITY_KEY command")
	void validSecurityKey() {
		var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.SECURITY_KEY, "webauthn-secret", null, "YubiKey", true);
		assertDoesNotThrow(() -> CreateMfaDevicesValidators.validate(cmd));
	}

	@Nested
	@DisplayName("invalid cases")
	class InvalidCases {

		@Test
		@DisplayName("should throw MFA_DEVICES_USER_ID_INVALID when userId is null")
		void invalidUserId() {
			var cmd = new CreateMfaDevicesCommand(null, MfaType.TOTP, "secret", null, "d", true);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_USER_ID_INVALID, ex.getCode());
		}

		@Test
		@DisplayName("should throw MFA_DEVICES_TYPE_INVALID when type is null")
		void invalidType() {
			var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, null, "secret", null, "d", true);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_TYPE_INVALID, ex.getCode());
		}

		static Stream<Arguments> blankDeviceNames() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("   "));
		}

		@ParameterizedTest(name = "should throw MFA_DEVICES_DEVICE_NAME_INVALID when deviceName is blank")
		@MethodSource("blankDeviceNames")
		void invalidDeviceName(String name) {
			var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.TOTP, "secret", null, name, true);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_DEVICE_NAME_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidTotpSecrets() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("   "));
		}

		@ParameterizedTest(name = "should throw MFA_DEVICES_SECRET_INVALID for TOTP when secret is blank")
		@MethodSource("invalidTotpSecrets")
		void invalidTotpSecret(String secret) {
			var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.TOTP, secret, null, "dev", true);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_SECRET_INVALID, ex.getCode());
		}

		@ParameterizedTest(name = "should throw MFA_DEVICES_SECRET_INVALID for SECURITY_KEY when secret is blank")
		@MethodSource("invalidTotpSecrets")
		void invalidSecurityKeySecret(String secret) {
			var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.SECURITY_KEY, secret, null, "dev", true);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_SECRET_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidSmsPhones() {
			return Stream.of(
					Arguments.of((String) null),
					Arguments.of(""),
					Arguments.of("+0123456789"),
					Arguments.of("not-a-phone"));
		}

		@ParameterizedTest(name = "should throw MFA_DEVICES_PHONE_INVALID for SMS when phone is invalid")
		@MethodSource("invalidSmsPhones")
		void invalidSmsPhone(String phone) {
			var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.SMS, null, phone, "dev", true);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_PHONE_INVALID, ex.getCode());
		}

		static Stream<Arguments> invalidEmailContacts() {
			return Stream.of(
					Arguments.of((String) null),
					Arguments.of(""),
					Arguments.of("bad-email"),
					Arguments.of("a@b"));
		}

		@ParameterizedTest(name = "should throw MFA_DEVICES_PHONE_INVALID for EMAIL when contact is invalid email")
		@MethodSource("invalidEmailContacts")
		void invalidEmailMfaContact(String contact) {
			var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.EMAIL, null, contact, "dev", true);
			BusinessError ex = assertThrows(BusinessError.class, () -> CreateMfaDevicesValidators.validate(cmd));
			assertEquals(CodesError.MFA_DEVICES_PHONE_INVALID, ex.getCode());
		}
	}

	@Nested
	@DisplayName("valid field variations")
	class ValidVariations {

		static Stream<Arguments> validPhones() {
			return Stream.of(Arguments.of("+19876543210"), Arguments.of("+4412345678901"));
		}

		@ParameterizedTest(name = "should NOT throw for SMS with phone \"{0}\"")
		@MethodSource("validPhones")
		void validSmsPhones(String phone) {
			var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.SMS, null, phone, "Phone", true);
			assertDoesNotThrow(() -> CreateMfaDevicesValidators.validate(cmd));
		}

		static Stream<Arguments> validEmails() {
			return Stream.of(Arguments.of("a@b.co"), Arguments.of("alias+tag@domain.example"));
		}

		@ParameterizedTest(name = "should NOT throw for EMAIL MFA with \"{0}\"")
		@MethodSource("validEmails")
		void validEmailContacts(String email) {
			var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.EMAIL, null, email, "Mail", true);
			assertDoesNotThrow(() -> CreateMfaDevicesValidators.validate(cmd));
		}

		static Stream<Arguments> validSecrets() {
			return Stream.of(Arguments.of("A"), Arguments.of("base32secret22"));
		}

		@ParameterizedTest(name = "should NOT throw for TOTP with non-blank secret")
		@MethodSource("validSecrets")
		void validTotpSecrets(String secret) {
			var cmd = new CreateMfaDevicesCommand(VALID_USER_ID, MfaType.TOTP, secret, null, "App", true);
			assertDoesNotThrow(() -> CreateMfaDevicesValidators.validate(cmd));
		}
	}
}
