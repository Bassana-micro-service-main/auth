package com.auth.application.dto.sessions;

import java.util.UUID;

/**
 * DTOs de recherche sessions (hors identifiant public).
 * Équivalents {@link com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByUserIdQuery}
 * et {@link com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByRefreshTokenQuery}.
 */
public final class ListSessionsDto {

	private ListSessionsDto() {
	}

	public record ByUserId(UUID userId) {
	}

	public record ByRefreshToken(String refreshToken) {
	}
}
