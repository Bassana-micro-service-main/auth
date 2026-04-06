package com.auth.domain.services.validators.credentials;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort.CreateCredentialsCommand;
import com.auth.lib.Utils;

/**
 * Validation métier de la commande de création d'identifiants.
 */
public final class CreateCredentialsValidators {

	private CreateCredentialsValidators() {
	}

	public static void validate(CreateCredentialsCommand command) {
		if (command.userId() == null) {
			throw new BusinessError(CodesError.CREDENTIALS_USER_ID_INVALID);
		}

		if (command.email() == null || !Utils.EMAIL_REGEX.matcher(command.email()).matches()) {
			throw new BusinessError(CodesError.CREDENTIALS_EMAIL_INVALID);
		}

		if (command.hashedPassword() == null
				|| !Utils.BCRYPT_HASH_REGEX.matcher(command.hashedPassword()).matches()) {
			throw new BusinessError(CodesError.CREDENTIALS_PASSWORD_INVALID);
		}

		if (command.passwordSalt() != null && command.passwordSalt().isBlank()) {
			throw new BusinessError(CodesError.CREDENTIALS_PASSWORD_INVALID);
		}
	}
}
