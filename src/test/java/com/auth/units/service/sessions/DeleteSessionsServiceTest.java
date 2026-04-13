package com.auth.units.service.sessions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort.DeleteSessionsCommand;
import com.auth.domain.services.validators.sessions.DeleteSessionsValidators;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("DeleteSessionsValidators (full coverage)")
class DeleteSessionsServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";

	@Test
	@DisplayName("should pass for valid publicId")
	void valid() {
		assertDoesNotThrow(() -> DeleteSessionsValidators.validate(new DeleteSessionsCommand(VALID_NANOID)));
	}

	static Stream<Arguments> invalidPublicIds() {
		return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"));
	}

	@ParameterizedTest(name = "should throw SESSIONS_PUBLIC_ID_INVALID when publicId is invalid")
	@MethodSource("invalidPublicIds")
	void invalidPublicId(String publicId) {
		var cmd = new DeleteSessionsCommand(publicId);
		BusinessError ex = assertThrows(BusinessError.class, () -> DeleteSessionsValidators.validate(cmd));
		assertEquals(CodesError.SESSIONS_PUBLIC_ID_INVALID, ex.getCode());
	}
}
