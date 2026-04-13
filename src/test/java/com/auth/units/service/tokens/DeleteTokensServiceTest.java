package com.auth.units.service.tokens;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.DeleteTokensInterfacePort.DeleteTokensCommand;
import com.auth.domain.services.validators.tokens.DeleteTokensValidators;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("DeleteTokensValidators (full coverage)")
class DeleteTokensServiceTest {

	private static final String VALID_NANOID = "abcdefghij12345678901";

	@Test
	@DisplayName("should pass for valid publicId")
	void valid() {
		assertDoesNotThrow(() -> DeleteTokensValidators.validate(new DeleteTokensCommand(VALID_NANOID)));
	}

	static Stream<Arguments> invalidPublicIds() {
		return Stream.of(Arguments.of((String) null), Arguments.of(""), Arguments.of("short"));
	}

	@ParameterizedTest(name = "should throw TOKENS_PUBLIC_ID_INVALID")
	@MethodSource("invalidPublicIds")
	void invalidPublicId(String publicId) {
		var cmd = new DeleteTokensCommand(publicId);
		BusinessError ex = assertThrows(BusinessError.class, () -> DeleteTokensValidators.validate(cmd));
		assertEquals(CodesError.TOKENS_PUBLIC_ID_INVALID, ex.getCode());
	}
}
