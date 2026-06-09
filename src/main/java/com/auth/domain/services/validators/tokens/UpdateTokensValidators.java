package com.auth.domain.services.validators.tokens;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort.UpdateTokensCommand;
import com.auth.domain.services.validators.InputValidators;
import java.time.Instant;

public final class UpdateTokensValidators {

	private UpdateTokensValidators() {
	}

	public static void validate(UpdateTokensCommand command) {
		InputValidators.requireNanoid(command.publicId(), CodesError.TOKENS_PUBLIC_ID_INVALID);

		command.value().ifPresent(v -> {
			if (v.isBlank()) {
				throw new BusinessError(CodesError.TOKENS_VALUE_INVALID);
			}
		});

		command.expiresAt().ifPresent(UpdateTokensValidators::requireFuture);
	}

	private static void requireFuture(Instant instant) {
		if (!instant.isAfter(Instant.now())) {
			throw new BusinessError(CodesError.TOKENS_EXPIRES_AT_INVALID);
		}
	}
}
