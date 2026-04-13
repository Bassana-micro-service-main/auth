package com.auth.adapter.in.auth;

import com.auth.application.auth.AuthSessionIssuer;
import com.auth.application.use_case.auth.LoginUseCase;
import com.auth.application.use_case.auth.LogoutUseCase;
import com.auth.application.use_case.auth.RefreshTokenUseCase;
import com.auth.application.use_case.auth.RegisterUseCase;
import com.auth.application.use_case.auth.VerifyAccessTokenUseCase;
import com.auth.config.AuthTokenProperties;
import com.auth.config.UserProfileProperties;
import com.auth.domain.ports.in.auth.LoginInterfacePort;
import com.auth.domain.ports.in.auth.LogoutInterfacePort;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort;
import com.auth.domain.ports.in.auth.RegisterInterfacePort;
import com.auth.domain.ports.in.auth.VerifyAccessTokenInterfacePort;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.domain.ports.out.PasswordCryptoPort;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.domain.ports.out.TokensRepositoryPort;
import com.auth.domain.ports.out.UserProfileClientPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

/**
 * Module authentification (use cases + client user_profile + crypto).
 */
@Configuration
@Import(AuthControllerAdapter.class)
@EnableConfigurationProperties({AuthTokenProperties.class, UserProfileProperties.class})
public class AuthModule {

	@Bean(name = "userProfileRestClient")
	public RestClient userProfileRestClient(UserProfileProperties props) {
		String base = props.getBaseUrl() == null || props.getBaseUrl().isBlank()
				? "http://127.0.0.1:1"
				: props.getBaseUrl();
		return RestClient.builder().baseUrl(base).build();
	}

	@Bean
	public LoginInterfacePort loginUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort credentials,
			@Qualifier(PasswordCryptoPort.QUALIFIER) PasswordCryptoPort passwordCrypto,
			@Qualifier(UserProfileClientPort.QUALIFIER) UserProfileClientPort userProfile,
			AuthSessionIssuer authSessionIssuer) {
		return new LoginUseCase(credentials, passwordCrypto, userProfile, authSessionIssuer);
	}

	@Bean
	public RegisterInterfacePort registerUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort credentials,
			CreateCredentialsInterfacePort createCredentials,
			@Qualifier(PasswordCryptoPort.QUALIFIER) PasswordCryptoPort passwordCrypto,
			@Qualifier(UserProfileClientPort.QUALIFIER) UserProfileClientPort userProfile,
			AuthSessionIssuer authSessionIssuer) {
		return new RegisterUseCase(credentials, createCredentials, passwordCrypto, userProfile, authSessionIssuer);
	}

	@Bean
	public LogoutInterfacePort logoutUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort sessions) {
		return new LogoutUseCase(sessions);
	}

	@Bean
	public RefreshTokenInterfacePort refreshTokenUseCase(
			@Qualifier(SessionsRepositoryPort.REPOSITORY_QUALIFIER) SessionsRepositoryPort sessions,
			AuthSessionIssuer authSessionIssuer) {
		return new RefreshTokenUseCase(sessions, authSessionIssuer);
	}

	@Bean
	public VerifyAccessTokenInterfacePort verifyAccessTokenUseCase(
			@Qualifier(TokensRepositoryPort.REPOSITORY_QUALIFIER) TokensRepositoryPort tokens) {
		return new VerifyAccessTokenUseCase(tokens);
	}
}
