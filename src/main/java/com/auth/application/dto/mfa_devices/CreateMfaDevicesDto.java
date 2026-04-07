package com.auth.application.dto.mfa_devices;

import com.auth.domain.enums.MfaType;
import java.util.UUID;

/**
 * DTO application pour {@link com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort.CreateMfaDevicesCommand}.
 */
public record CreateMfaDevicesDto(
		UUID userId,
		MfaType type,
		String secret,
		String phoneNumber,
		String deviceName,
		boolean active
) {
}
