package com.auth.infrastructure.database.hibernate.enums;

import java.util.Arrays;

public enum TokenType {
	ACCESS("access"),
	REFRESH("refresh"),
	PASSWORD_RESET("password_reset"),
	EMAIL_VERIFICATION("email_verification"),
	API_KEY("api_key");

	private final String value;

	TokenType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static TokenType fromValue(String value) {
		return Arrays.stream(values())
				.filter(v -> v.value.equals(value))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown token type: " + value));
	}
}
