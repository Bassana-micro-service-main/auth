package com.auth.application.dto.auth;

/**
 * Corps {@code POST /auth/login}.
 */
public record LoginRequestDto(String email, String password, String deviceName) {
}
