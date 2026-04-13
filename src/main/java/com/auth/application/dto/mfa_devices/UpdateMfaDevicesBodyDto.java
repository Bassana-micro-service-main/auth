package com.auth.application.dto.mfa_devices;

import com.auth.domain.enums.MfaType;
import java.time.Instant;
import java.util.Optional;

/**
 * Corps PATCH : le {@code publicId} est dans le chemin.
 */
public record UpdateMfaDevicesBodyDto(
		Optional<MfaType> type,
		Optional<String> secret,
		Optional<String> phoneNumber,
		Optional<String> deviceName,
		Optional<Boolean> active,
		Optional<Instant> lastUsedAt
) {
	public UpdateMfaDevicesBodyDto {
		type = type != null ? type : Optional.empty();
		secret = secret != null ? secret : Optional.empty();
		phoneNumber = phoneNumber != null ? phoneNumber : Optional.empty();
		deviceName = deviceName != null ? deviceName : Optional.empty();
		active = active != null ? active : Optional.empty();
		lastUsedAt = lastUsedAt != null ? lastUsedAt : Optional.empty();
	}
}
