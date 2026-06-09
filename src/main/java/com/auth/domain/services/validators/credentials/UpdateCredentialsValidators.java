package com.auth.domain.services.validators.credentials;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort.UpdateCredentialsCommand;
import com.auth.domain.services.validators.InputValidators;
import com.auth.lib.Utils;

public final class UpdateCredentialsValidators {

	private UpdateCredentialsValidators() {
	}

	public static void validate(UpdateCredentialsCommand command) {
		InputValidators.requireNanoid(command.publicId(), CodesError.CREDENTIALS_PUBLIC_ID_INVALID);

		command.email().ifPresent(email -> {
			if (!Utils.EMAIL_REGEX.matcher(email).matches()) {
				throw new BusinessError(CodesError.CREDENTIALS_EMAIL_INVALID);
			}
		});

		command.hashedPassword().ifPresent(hash -> {
			if (!Utils.BCRYPT_HASH_REGEX.matcher(hash).matches()) {
				throw new BusinessError(CodesError.CREDENTIALS_PASSWORD_INVALID);
			}
		});

		command.passwordSalt().ifPresent(salt -> {
			if (salt.isBlank()) {
				throw new BusinessError(CodesError.CREDENTIALS_PASSWORD_INVALID);
			}
		});
	}
}
