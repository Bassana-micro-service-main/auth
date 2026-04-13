package com.auth.application.dto.auth;

/**
 * Corps {@code POST /auth/logout}.
 */
public record LogoutRequestDto(String refreshToken) {
}
