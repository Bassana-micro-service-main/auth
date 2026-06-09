package com.auth.application.dto.sessions;

/**
 * Recherche par {@code publicId} seul. Les autres critères : {@link ListSessionsDto}.
 */
public record GetSessionsDto(String publicId) {
}
