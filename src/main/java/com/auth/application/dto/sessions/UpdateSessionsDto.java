package com.auth.application.dto.sessions;

import java.time.Instant;
import java.util.Optional;

/**
 * DTO application pour {@link com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort.UpdateSessionsCommand}.
 */
public record UpdateSessionsDto(
		String publicId,
		Optional<String> ipAddress,
		Optional<String> userAgent,
		Optional<String> deviceName,
		Optional<String> refreshToken,
		Optional<Instant> expiresAt,
		Optional<Boolean> revoked
) {
}
