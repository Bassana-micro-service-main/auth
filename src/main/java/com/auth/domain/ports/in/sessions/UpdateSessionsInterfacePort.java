package com.auth.domain.ports.in.sessions;

import com.auth.domain.entities.Session;
import java.time.Instant;
import java.util.Optional;

/**
 * Port entrant (driving) : mise à jour d'une entité {@link Session}.
 * Les champs absents ({@link Optional#empty()}) signifient « ne pas modifier ».
 */
public interface UpdateSessionsInterfacePort {

	record UpdateSessionsCommand(
			String publicId,
			Optional<String> ipAddress,
			Optional<String> userAgent,
			Optional<String> deviceName,
			Optional<String> refreshToken,
			Optional<Instant> expiresAt,
			Optional<Boolean> revoked
	) {
	}

	Session update(UpdateSessionsCommand command);
}
