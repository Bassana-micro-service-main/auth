package com.auth.application.dto.sessions;

import java.time.Instant;
import java.util.Optional;

/**
 * Corps PATCH : le {@code publicId} est dans le chemin.
 */
public record UpdateSessionsBodyDto(
		Optional<String> ipAddress,
		Optional<String> userAgent,
		Optional<String> deviceName,
		Optional<String> refreshToken,
		Optional<Instant> expiresAt,
		Optional<Boolean> revoked
) {
	public UpdateSessionsBodyDto {
		ipAddress = ipAddress != null ? ipAddress : Optional.empty();
		userAgent = userAgent != null ? userAgent : Optional.empty();
		deviceName = deviceName != null ? deviceName : Optional.empty();
		refreshToken = refreshToken != null ? refreshToken : Optional.empty();
		expiresAt = expiresAt != null ? expiresAt : Optional.empty();
		revoked = revoked != null ? revoked : Optional.empty();
	}
}
