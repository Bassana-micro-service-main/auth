package com.auth.application.dto.mfa_devices;

import com.auth.domain.enums.MfaType;
import java.time.Instant;
import java.util.Optional;

/**
 * DTO application pour {@link com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort.UpdateMfaDevicesCommand}.
 */
public record UpdateMfaDevicesDto(
		String publicId,
		Optional<MfaType> type,
		Optional<String> secret,
		Optional<String> phoneNumber,
		Optional<String> deviceName,
		Optional<Boolean> active,
		Optional<Instant> lastUsedAt
) {
}
