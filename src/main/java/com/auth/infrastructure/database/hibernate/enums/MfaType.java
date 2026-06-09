package com.auth.infrastructure.database.hibernate.enums;

import java.util.Arrays;

public enum MfaType {
	TOTP("totp"),
	SMS("sms"),
	EMAIL("email"),
	SECURITY_KEY("security_key");

	private final String value;

	MfaType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static MfaType fromValue(String value) {
		return Arrays.stream(values())
				.filter(v -> v.value.equals(value))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown MFA type: " + value));
	}
}
