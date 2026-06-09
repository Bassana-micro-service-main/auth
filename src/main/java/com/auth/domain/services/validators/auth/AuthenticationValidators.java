package com.auth.domain.services.validators.auth;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.LoginInterfacePort.LoginCommand;
import com.auth.domain.ports.in.auth.LogoutInterfacePort.LogoutCommand;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort.RefreshTokenCommand;
import com.auth.domain.ports.in.auth.RegisterInterfacePort.RegisterCommand;
import com.auth.domain.services.validators.InputValidators;
import com.auth.lib.Utils;

/**
 * Validation de forme des commandes d’authentification (login, register, logout, refresh).
 */
public final class AuthenticationValidators {

	private AuthenticationValidators() {
	}

	public static void validate(LoginCommand command) {
		if (command.email() == null || !Utils.EMAIL_REGEX.matcher(command.email()).matches()) {
			throw new BusinessError(CodesError.CREDENTIALS_EMAIL_INVALID);
		}
		InputValidators.requireNonBlank(command.plainPassword(), CodesError.CREDENTIALS_PASSWORD_INVALID);
	}

	public static void validate(RegisterCommand command) {
		if (command.email() == null || !Utils.EMAIL_REGEX.matcher(command.email()).matches()) {
			throw new BusinessError(CodesError.CREDENTIALS_EMAIL_INVALID);
		}
		if (command.plainPassword() == null || !Utils.PASSWORD_REGEX.matcher(command.plainPassword()).matches()) {
			throw new BusinessError(CodesError.CREDENTIALS_PASSWORD_INVALID);
		}
	}

	public static void validate(LogoutCommand command) {
		InputValidators.requireNonBlank(command.refreshToken(), CodesError.SESSIONS_REFRESH_TOKEN_INVALID);
	}

	public static void validate(RefreshTokenCommand command) {
		InputValidators.requireNonBlank(command.refreshToken(), CodesError.SESSIONS_REFRESH_TOKEN_INVALID);
	}
}
