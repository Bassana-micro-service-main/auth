package com.auth.application.dto.credentials;

import java.util.UUID;

/**
 * DTO application pour {@link com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort.CreateCredentialsCommand}.
 */
public record CreateCredentialsDto(
		UUID userId,
		String email,
		String hashedPassword,
		String passwordSalt
) {
}
