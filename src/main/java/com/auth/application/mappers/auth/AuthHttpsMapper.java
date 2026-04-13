package com.auth.application.mappers.auth;

import com.auth.application.dto.auth.AuthResponseDto;
import com.auth.domain.ports.in.auth.AuthSessionResult;

/**
 * Mapper HTTP ↔ résultats d’authentification.
 */
public final class AuthHttpsMapper {

	private AuthHttpsMapper() {
	}

	public static AuthResponseDto toResponse(AuthSessionResult result) {
		if (result == null) {
			return null;
		}
		return new AuthResponseDto(
				result.accessToken(),
				result.refreshToken(),
				result.accessExpiresAt(),
				result.refreshExpiresAt(),
				result.sessionPublicId());
	}
}
