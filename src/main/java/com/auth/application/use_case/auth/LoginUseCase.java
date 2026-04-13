package com.auth.application.use_case.auth;

import com.auth.application.auth.AuthSessionIssuer;
import com.auth.domain.entities.Credential;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.AuthSessionResult;
import com.auth.domain.ports.in.auth.LoginInterfacePort;
import com.auth.domain.ports.in.auth.LoginInterfacePort.LoginCommand;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.domain.ports.out.PasswordCryptoPort;
import com.auth.domain.ports.out.UserProfileClientPort;
import com.auth.domain.services.validators.auth.AuthenticationValidators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Connexion : vérifie email / mot de passe et émet session + jetons.
 */
@Transactional
public class LoginUseCase implements LoginInterfacePort {

	private final CredentialsRepositoryPort credentials;
	private final PasswordCryptoPort passwordCrypto;
	private final UserProfileClientPort userProfile;
	private final AuthSessionIssuer authSessionIssuer;

	public LoginUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort credentials,
			@Qualifier(PasswordCryptoPort.QUALIFIER) PasswordCryptoPort passwordCrypto,
			@Qualifier(UserProfileClientPort.QUALIFIER) UserProfileClientPort userProfile,
			AuthSessionIssuer authSessionIssuer) {
		this.credentials = credentials;
		this.passwordCrypto = passwordCrypto;
		this.userProfile = userProfile;
		this.authSessionIssuer = authSessionIssuer;
	}

	@Override
	public AuthSessionResult login(LoginCommand command) {
		AuthenticationValidators.validate(command);
		Credential credential = credentials
				.findByEmail(command.email())
				.orElseThrow(() -> new BusinessError(CodesError.AUTH_INVALID_CREDENTIALS));
		if (!credential.isActive()) {
			throw new BusinessError(CodesError.CREDENTIALS_INACTIVE);
		}
		if (!passwordCrypto.matches(command.plainPassword(), credential.getHashedPassword())) {
			throw new BusinessError(CodesError.AUTH_INVALID_CREDENTIALS);
		}
		userProfile.assertUserActive(credential.getUserId());
		return authSessionIssuer.issueSessionAndTokens(
				credential.getUserId(),
				command.ipAddress(),
				command.userAgent(),
				command.deviceName());
	}
}
