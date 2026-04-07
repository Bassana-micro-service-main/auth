package com.auth.application.dto.tokens;

/**
 * Recherche par {@code publicId} seul. Les autres critères : {@link ListTokensDto}.
 */
public record GetTokensDto(String publicId) {
}
