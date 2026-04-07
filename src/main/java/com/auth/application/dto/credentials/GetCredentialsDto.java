package com.auth.application.dto.credentials;

/**
 * Recherche par {@code publicId} seul. Les autres critères : {@link CredentialsFindListDto}.
 */
public record GetCredentialsDto(String publicId) {
}
