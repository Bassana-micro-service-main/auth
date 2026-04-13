package com.auth.units.service.mfa_devices;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort.DeleteMfaDevicesCommand;
import com.auth.domain.services.validators.mfa_devices.DeleteMfaDevicesValidators;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("DeleteMfaDevicesValidators (full coverage)")
class DeleteMfaDevicesServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";

	@Test
	@DisplayName("should pass for valid publicId")
	void valid() {
		assertDoesNotThrow(() -> DeleteMfaDevicesValidators.validate(new DeleteMfaDevicesCommand(VALID_NANOID)));
	}

	static Stream<Arguments> invalidPublicIds() {
		return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"));
	}

	@ParameterizedTest(name = "should throw MFA_DEVICES_PUBLIC_ID_INVALID")
	@MethodSource("invalidPublicIds")
	void invalidPublicId(String publicId) {
		var cmd = new DeleteMfaDevicesCommand(publicId);
		BusinessError ex = assertThrows(BusinessError.class, () -> DeleteMfaDevicesValidators.validate(cmd));
		assertEquals(CodesError.MFA_DEVICES_PUBLIC_ID_INVALID, ex.getCode());
	}
}
