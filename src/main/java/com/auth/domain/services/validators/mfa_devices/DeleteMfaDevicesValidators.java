package com.auth.domain.services.validators.mfa_devices;

import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort.DeleteMfaDevicesCommand;
import com.auth.domain.services.validators.InputValidators;

public final class DeleteMfaDevicesValidators {

	private DeleteMfaDevicesValidators() {
	}

	public static void validate(DeleteMfaDevicesCommand command) {
		InputValidators.requireNanoid(command.publicId(), CodesError.MFA_DEVICES_PUBLIC_ID_INVALID);
	}
}
