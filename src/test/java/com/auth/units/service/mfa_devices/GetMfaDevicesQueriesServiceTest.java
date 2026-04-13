package com.auth.units.service.mfa_devices;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.enums.MfaType;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdAndTypeQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdQuery;
import com.auth.domain.services.validators.mfa_devices.GetMfaDevicesQueriesValidators;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("GetMfaDevicesQueriesValidators (full coverage)")
class GetMfaDevicesQueriesServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";
	private static final UUID VALID_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Nested
	@DisplayName("FindByPublicIdQuery")
	class FindByPublicId {

		static Stream<Arguments> invalidPublicIds() {
			return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"));
		}

		@ParameterizedTest(name = "should throw MFA_DEVICES_PUBLIC_ID_INVALID")
		@MethodSource("invalidPublicIds")
		void invalid(String publicId) {
			var q = new FindByPublicIdQuery(publicId);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetMfaDevicesQueriesValidators.validate(q));
			assertEquals(CodesError.MFA_DEVICES_PUBLIC_ID_INVALID, ex.getCode());
		}

		@Test
		void valid() {
			assertDoesNotThrow(() -> GetMfaDevicesQueriesValidators.validate(new FindByPublicIdQuery(VALID_NANOID)));
		}
	}

	@Nested
	@DisplayName("FindByUserIdQuery")
	class FindByUserId {

		@Test
		void invalidNullUserId() {
			var q = new FindByUserIdQuery(null);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetMfaDevicesQueriesValidators.validate(q));
			assertEquals(CodesError.MFA_DEVICES_USER_ID_INVALID, ex.getCode());
		}

		@Test
		void valid() {
			assertDoesNotThrow(() -> GetMfaDevicesQueriesValidators.validate(new FindByUserIdQuery(VALID_USER_ID)));
		}
	}

	@Nested
	@DisplayName("FindByUserIdAndTypeQuery")
	class FindByUserIdAndType {

		@Test
		void invalidNullUserId() {
			var q = new FindByUserIdAndTypeQuery(null, MfaType.TOTP);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetMfaDevicesQueriesValidators.validate(q));
			assertEquals(CodesError.MFA_DEVICES_USER_ID_INVALID, ex.getCode());
		}

		@Test
		void invalidNullType() {
			var q = new FindByUserIdAndTypeQuery(VALID_USER_ID, null);
			BusinessError ex = assertThrows(BusinessError.class, () -> GetMfaDevicesQueriesValidators.validate(q));
			assertEquals(CodesError.MFA_DEVICES_TYPE_INVALID, ex.getCode());
		}

		@Test
		void valid() {
			assertDoesNotThrow(
					() -> GetMfaDevicesQueriesValidators.validate(new FindByUserIdAndTypeQuery(VALID_USER_ID, MfaType.SMS)));
		}
	}
}
