package com.auth.domain.services.validators.credentials;

import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort.DeleteCredentialsCommand;
import com.auth.domain.services.validators.InputValidators;

public final class DeleteCredentialsValidators {

	private DeleteCredentialsValidators() {
	}

	public static void validate(DeleteCredentialsCommand command) {
		InputValidators.requireNanoid(command.publicId(), CodesError.CREDENTIALS_PUBLIC_ID_INVALID);
	}
}
