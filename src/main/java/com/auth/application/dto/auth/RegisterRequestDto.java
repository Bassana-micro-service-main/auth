package com.auth.application.dto.auth;

/**
 * Corps {@code POST /auth/register}.
 */
public record RegisterRequestDto(String email, String password, String deviceName) {
}
