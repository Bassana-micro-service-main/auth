package com.auth.application.use_case.auth;

import com.auth.application.auth.AuthSessionIssuer;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.auth.AuthSessionResult;
import com.auth.domain.ports.in.auth.RegisterInterfacePort;
import com.auth.domain.ports.in.auth.RegisterInterfacePort.RegisterCommand;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort.CreateCredentialsCommand;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.domain.ports.out.PasswordCryptoPort;
import com.auth.domain.ports.out.UserProfileClientPort;
import com.auth.domain.services.validators.auth.AuthenticationValidators;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inscription : création utilisateur côté user_profile puis credentials locaux, puis session.
 */
@Transactional
public class RegisterUseCase implements RegisterInterfacePort {

	private final CredentialsRepositoryPort credentials;
	private final CreateCredentialsInterfacePort createCredentials;
	private final PasswordCryptoPort passwordCrypto;
	private final UserProfileClientPort userProfile;
	private final AuthSessionIssuer authSessionIssuer;

	public RegisterUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort credentials,
			CreateCredentialsInterfacePort createCredentials,
			@Qualifier(PasswordCryptoPort.QUALIFIER) PasswordCryptoPort passwordCrypto,
			@Qualifier(UserProfileClientPort.QUALIFIER) UserProfileClientPort userProfile,
			AuthSessionIssuer authSessionIssuer) {
		this.credentials = credentials;
		this.createCredentials = createCredentials;
		this.passwordCrypto = passwordCrypto;
		this.userProfile = userProfile;
		this.authSessionIssuer = authSessionIssuer;
	}

	@Override
	public AuthSessionResult register(RegisterCommand command) {
		AuthenticationValidators.validate(command);
		if (credentials.findByEmail(command.email()).isPresent()) {
			throw new BusinessError(CodesError.AUTH_EMAIL_ALREADY_REGISTERED);
		}
		UUID userId = userProfile.createUser(command.email());
		String hashed = passwordCrypto.hash(command.plainPassword());
		CreateCredentialsCommand createCmd = new CreateCredentialsCommand(
				userId, command.email(), hashed, null);
		createCredentials.create(createCmd);
		return authSessionIssuer.issueSessionAndTokens(
				userId, command.ipAddress(), command.userAgent(), command.deviceName());
	}
}
