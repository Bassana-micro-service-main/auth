package com.auth.domain.services.validators.tokens;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort.CreateTokensCommand;
import com.auth.domain.services.validators.InputValidators;

public final class CreateTokensValidators {

	private CreateTokensValidators() {
	}

	public static void validate(CreateTokensCommand command) {
		if (command.type() == null) {
			throw new BusinessError(CodesError.TOKENS_TYPE_INVALID);
		}
		InputValidators.requireNonBlank(command.value(), CodesError.TOKENS_VALUE_INVALID);
		InputValidators.requireFutureInstant(command.expiresAt(), CodesError.TOKENS_EXPIRES_AT_INVALID);
	}
}
