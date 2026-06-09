package com.auth.application.dto.credentials;

import java.util.Optional;

/**
 * Corps PATCH : le {@code publicId} est porté par le chemin, pas par le JSON.
 */
public record UpdateCredentialsBodyDto(
		Optional<String> email,
		Optional<String> hashedPassword,
		Optional<String> passwordSalt,
		Optional<Boolean> active
) {
	public UpdateCredentialsBodyDto {
		email = email != null ? email : Optional.empty();
		hashedPassword = hashedPassword != null ? hashedPassword : Optional.empty();
		passwordSalt = passwordSalt != null ? passwordSalt : Optional.empty();
		active = active != null ? active : Optional.empty();
	}
}
