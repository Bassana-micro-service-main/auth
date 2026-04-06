package com.auth.domain.services.validators.sessions;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort.UpdateSessionsCommand;
import com.auth.domain.services.validators.InputValidators;
import java.time.Instant;

public final class UpdateSessionsValidators {

	private UpdateSessionsValidators() {
	}

	public static void validate(UpdateSessionsCommand command) {
		InputValidators.requireNanoid(command.publicId(), CodesError.SESSIONS_PUBLIC_ID_INVALID);

		InputValidators.requireOptionalNonBlankIfPresent(command.ipAddress(), CodesError.SESSIONS_IP_ADDRESS_INVALID);
		InputValidators.requireOptionalNonBlankIfPresent(command.userAgent(), CodesError.SESSIONS_USER_AGENT_INVALID);
		InputValidators.requireOptionalNonBlankIfPresent(command.deviceName(), CodesError.SESSIONS_DEVICE_NAME_INVALID);
		InputValidators.requireOptionalNonBlankIfPresent(command.refreshToken(), CodesError.SESSIONS_REFRESH_TOKEN_INVALID);

		command.expiresAt().ifPresent(instant -> validateExpiresAt(instant));
	}

	private static void validateExpiresAt(Instant instant) {
		if (!instant.isAfter(Instant.now())) {
			throw new BusinessError(CodesError.SESSIONS_EXPIRES_AT_INVALID);
		}
	}
}
