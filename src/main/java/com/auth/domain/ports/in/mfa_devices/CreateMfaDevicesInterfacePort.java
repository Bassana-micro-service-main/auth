package com.auth.domain.ports.in.mfa_devices;

import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import java.util.UUID;

/**
 * Port entrant (driving) : création d'une entité {@link MfaDevice}.
 */
public interface CreateMfaDevicesInterfacePort {

	record CreateMfaDevicesCommand(
			UUID userId,
			MfaType type,
			String secret,
			String phoneNumber,
			String deviceName,
			boolean active
	) {
	}

	MfaDevice create(CreateMfaDevicesCommand command);
}
