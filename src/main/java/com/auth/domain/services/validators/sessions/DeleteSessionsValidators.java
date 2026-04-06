package com.auth.domain.services.validators.sessions;

import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort.DeleteSessionsCommand;
import com.auth.domain.services.validators.InputValidators;

public final class DeleteSessionsValidators {

	private DeleteSessionsValidators() {
	}

	public static void validate(DeleteSessionsCommand command) {
		InputValidators.requireNanoid(command.publicId(), CodesError.SESSIONS_PUBLIC_ID_INVALID);
	}
}
