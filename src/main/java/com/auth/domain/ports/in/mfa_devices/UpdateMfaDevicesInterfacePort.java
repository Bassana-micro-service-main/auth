package com.auth.domain.ports.in.mfa_devices;

import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import java.time.Instant;
import java.util.Optional;

/**
 * Port entrant (driving) : mise à jour d'une entité {@link MfaDevice}.
 * Les champs absents ({@link Optional#empty()}) signifient « ne pas modifier ».
 */
public interface UpdateMfaDevicesInterfacePort {

	record UpdateMfaDevicesCommand(
			String publicId,
			Optional<MfaType> type,
			Optional<String> secret,
			Optional<String> phoneNumber,
			Optional<String> deviceName,
			Optional<Boolean> active,
			Optional<Instant> lastUsedAt
	) {
	}

	MfaDevice update(UpdateMfaDevicesCommand command);
}
