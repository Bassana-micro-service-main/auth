package com.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Durées des jetons d’accès / refresh et de session (alignées par défaut).
 */
@ConfigurationProperties(prefix = "auth.tokens")
public class AuthTokenProperties {

	private int accessTokenMinutes = 15;
	private int refreshTokenDays = 30;

	public int getAccessTokenMinutes() {
		return accessTokenMinutes;
	}

	public void setAccessTokenMinutes(int accessTokenMinutes) {
		this.accessTokenMinutes = accessTokenMinutes;
	}

	public int getRefreshTokenDays() {
		return refreshTokenDays;
	}

	public void setRefreshTokenDays(int refreshTokenDays) {
		this.refreshTokenDays = refreshTokenDays;
	}
}
