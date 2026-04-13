package com.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Accès HTTP au service <em>user_profile</em> (base URL + chemins REST).
 */
@ConfigurationProperties(prefix = "auth.user-profile")
public class UserProfileProperties {

	/**
	 * Ex. {@code http://user-profile:8080}. Si vide, {@link com.auth.adapter.out.user_profile.UserProfileRestAdapter}
	 * refuse la création d’utilisateur ; le login peut ignorer {@code assertUserActive}.
	 */
	private String baseUrl = "";

	private String createUserPath = "/api/internal/users";

	private String getUserPath = "/api/internal/users/{userId}";

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getCreateUserPath() {
		return createUserPath;
	}

	public void setCreateUserPath(String createUserPath) {
		this.createUserPath = createUserPath;
	}

	public String getGetUserPath() {
		return getUserPath;
	}

	public void setGetUserPath(String getUserPath) {
		this.getUserPath = getUserPath;
	}
}
