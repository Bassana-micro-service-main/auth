package com.auth.application.dto.credentials;

import java.util.UUID;

/**
 * DTOs de recherche credentials (hors identifiant public).
 * Équivalents {@link com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByEmailQuery}
 * et {@link com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByUserIdQuery}.
 */
public final class ListCredentialsDto {

	private ListCredentialsDto() {
	}

	public record ByEmail(String email) {
	}

	public record ByUserId(UUID userId) {
	}
}
