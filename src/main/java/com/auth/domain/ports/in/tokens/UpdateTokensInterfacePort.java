package com.auth.domain.ports.in.tokens;

import com.auth.domain.entities.Token;
import java.time.Instant;
import java.util.Optional;

/**
 * Port entrant (driving) : mise à jour d'une entité {@link Token}.
 * Les champs absents ({@link Optional#empty()}) signifient « ne pas modifier ».
 */
public interface UpdateTokensInterfacePort {

	record UpdateTokensCommand(
			String publicId,
			Optional<String> value,
			Optional<Instant> expiresAt
	) {
	}

	Token update(UpdateTokensCommand command);
}
