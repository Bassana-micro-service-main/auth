package com.auth.domain.services.validators.tokens;

import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.DeleteTokensInterfacePort.DeleteTokensCommand;
import com.auth.domain.services.validators.InputValidators;

public final class DeleteTokensValidators {

	private DeleteTokensValidators() {
	}

	public static void validate(DeleteTokensCommand command) {
		InputValidators.requireNanoid(command.publicId(), CodesError.TOKENS_PUBLIC_ID_INVALID);
	}
}
