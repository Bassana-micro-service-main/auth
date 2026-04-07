package com.auth.application.dto.credentials;

import java.util.Optional;

/**
 * DTO application pour {@link com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort.UpdateCredentialsCommand}.
 */
public record UpdateCredentialsDto(
		String publicId,
		Optional<String> email,
		Optional<String> hashedPassword,
		Optional<String> passwordSalt,
		Optional<Boolean> active
) {
}
