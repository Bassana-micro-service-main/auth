package com.auth.domain.ports.in.credentials;

import com.auth.domain.entities.Credential;
import java.util.Optional;

/**
 * Port entrant (driving) : mise à jour d'une entité {@link Credential}.
 * Les champs absents ({@link Optional#empty()}) signifient « ne pas modifier ».
 */
public interface UpdateCredentialsInterfacePort {

	record UpdateCredentialsCommand(
			String publicId,
			Optional<String> email,
			Optional<String> hashedPassword,
			Optional<String> passwordSalt,
			Optional<Boolean> active
	) {
	}

	Credential update(UpdateCredentialsCommand command);
}
