package com.auth.application.dto.mfa_devices;

import com.auth.domain.enums.MfaType;
import java.util.UUID;

/**
 * DTOs de recherche MFA (hors identifiant public).
 * Équivalents {@link com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdQuery}
 * et {@link com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdAndTypeQuery}.
 */
public final class ListMfaDevicesDto {

	private ListMfaDevicesDto() {
	}

	public record ByUserId(UUID userId) {
	}

	public record ByUserIdAndType(UUID userId, MfaType type) {
	}
}
