package com.auth.domain.services.validators.sessions;

import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort.CreateSessionsCommand;
import com.auth.domain.services.validators.InputValidators;

public final class CreateSessionsValidators {

	private CreateSessionsValidators() {
	}

	public static void validate(CreateSessionsCommand command) {
		InputValidators.requireNonNullUuid(command.userId(), CodesError.SESSIONS_USER_ID_INVALID);
		InputValidators.requireNonBlank(command.ipAddress(), CodesError.SESSIONS_IP_ADDRESS_INVALID);
		InputValidators.requireNonBlank(command.userAgent(), CodesError.SESSIONS_USER_AGENT_INVALID);
		InputValidators.requireNonBlank(command.deviceName(), CodesError.SESSIONS_DEVICE_NAME_INVALID);
		InputValidators.requireNonBlank(command.refreshToken(), CodesError.SESSIONS_REFRESH_TOKEN_INVALID);
		InputValidators.requireFutureInstant(command.expiresAt(), CodesError.SESSIONS_EXPIRES_AT_INVALID);
	}
}
