package com.auth.domain.services.validators.mfa_devices;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.enums.MfaType;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort.CreateMfaDevicesCommand;
import com.auth.domain.services.validators.InputValidators;
import com.auth.lib.Utils;

public final class CreateMfaDevicesValidators {

	private CreateMfaDevicesValidators() {
	}

	public static void validate(CreateMfaDevicesCommand command) {
		InputValidators.requireNonNullUuid(command.userId(), CodesError.MFA_DEVICES_USER_ID_INVALID);

		if (command.type() == null) {
			throw new BusinessError(CodesError.MFA_DEVICES_TYPE_INVALID);
		}

		InputValidators.requireNonBlank(command.deviceName(), CodesError.MFA_DEVICES_DEVICE_NAME_INVALID);

		switch (command.type()) {
			case TOTP, SECURITY_KEY -> {
				if (command.secret() == null || command.secret().isBlank()) {
					throw new BusinessError(CodesError.MFA_DEVICES_SECRET_INVALID);
				}
			}
			case SMS -> {
				if (command.phoneNumber() == null || !Utils.PHONE_REGEX.matcher(command.phoneNumber()).matches()) {
					throw new BusinessError(CodesError.MFA_DEVICES_PHONE_INVALID);
				}
			}
			case EMAIL -> {
				if (command.phoneNumber() == null || !Utils.EMAIL_REGEX.matcher(command.phoneNumber()).matches()) {
					throw new BusinessError(CodesError.MFA_DEVICES_PHONE_INVALID);
				}
			}
		}
	}
}
