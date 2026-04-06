package com.auth.domain.services.validators.mfa_devices;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort.UpdateMfaDevicesCommand;
import com.auth.domain.services.validators.InputValidators;
import com.auth.lib.Utils;

public final class UpdateMfaDevicesValidators {

	private UpdateMfaDevicesValidators() {
	}

	public static void validate(UpdateMfaDevicesCommand command) {
		InputValidators.requireNanoid(command.publicId(), CodesError.MFA_DEVICES_PUBLIC_ID_INVALID);

		command.secret().ifPresent(s -> {
			if (s.isBlank()) {
				throw new BusinessError(CodesError.MFA_DEVICES_SECRET_INVALID);
			}
		});

		command.phoneNumber().ifPresent(UpdateMfaDevicesValidators::validateContact);

		InputValidators.requireOptionalNonBlankIfPresent(command.deviceName(), CodesError.MFA_DEVICES_DEVICE_NAME_INVALID);
	}

	private static void validateContact(String value) {
		if (value.isBlank()) {
			throw new BusinessError(CodesError.MFA_DEVICES_PHONE_INVALID);
		}
		boolean ok = value.contains("@")
				? Utils.EMAIL_REGEX.matcher(value).matches()
				: Utils.PHONE_REGEX.matcher(value).matches();
		if (!ok) {
			throw new BusinessError(CodesError.MFA_DEVICES_PHONE_INVALID);
		}
	}
}
