package com.auth.application.dto.mfa_devices;

import com.auth.domain.enums.MfaType;
import java.time.Instant;
import java.util.UUID;

/**
 * Réponse HTTP pour un périphérique MFA (sans secret).
 */
public record MfaDevicesResponseDto(
		String publicId,
		UUID userId,
		MfaType type,
		String phoneNumber,
		String deviceName,
		boolean active,
		Instant lastUsedAt,
		Instant createdAt
) {
}
