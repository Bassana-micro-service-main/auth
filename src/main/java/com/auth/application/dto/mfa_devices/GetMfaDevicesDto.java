package com.auth.application.dto.mfa_devices;

/**
 * Recherche par {@code publicId} seul. Les autres critères : {@link ListMfaDevicesDto}.
 */
public record GetMfaDevicesDto(String publicId) {
}
