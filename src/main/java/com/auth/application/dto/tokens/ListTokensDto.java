package com.auth.application.dto.tokens;

import com.auth.domain.enums.TokenType;

/**
 * DTOs de recherche tokens (hors identifiant public).
 * Équivalents {@link com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByTypeQuery}
 * et {@link com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByValueQuery}.
 */
public final class ListTokensDto {

	private ListTokensDto() {
	}

	public record ByType(TokenType type) {
	}

	public record ByValue(String value) {
	}
}
