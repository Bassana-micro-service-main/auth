package com.auth.units.service.credentials;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort.DeleteCredentialsCommand;
import com.auth.domain.services.validators.credentials.DeleteCredentialsValidators;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("DeleteCredentialsValidators (full coverage)")
class DeleteCredentialsServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";

	@Test
	@DisplayName("should pass validation for valid publicId")
	void validCommand() {
		assertDoesNotThrow(() -> DeleteCredentialsValidators.validate(new DeleteCredentialsCommand(VALID_NANOID)));
	}

	static Stream<Arguments> invalidPublicIds() {
		return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"), Arguments.of("a".repeat(22)));
	}

	@ParameterizedTest(name = "should throw CREDENTIALS_PUBLIC_ID_INVALID when publicId is \"{0}\"")
	@MethodSource("invalidPublicIds")
	void invalidPublicId(String publicId) {
		var cmd = new DeleteCredentialsCommand(publicId);
		BusinessError ex = assertThrows(BusinessError.class, () -> DeleteCredentialsValidators.validate(cmd));
		assertEquals(CodesError.CREDENTIALS_PUBLIC_ID_INVALID, ex.getCode());
	}
}
