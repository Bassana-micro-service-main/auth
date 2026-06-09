package com.auth.application.use_case.credentials;

import com.auth.domain.entities.Credential;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort.CreateCredentialsCommand;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.domain.services.validators.credentials.CreateCredentialsValidators;
import com.auth.lib.Utils;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Création d’identifiants de connexion.
 */
@Transactional
public class CreateCredentialsUseCase implements CreateCredentialsInterfacePort {

	private final CredentialsRepositoryPort repository;

	public CreateCredentialsUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Credential create(CreateCredentialsCommand command) {
		CreateCredentialsValidators.validate(command);
		Credential credential = new Credential();
		credential.setPublicId(Utils.newNanoid());
		credential.setUserId(command.userId());
		credential.setEmail(command.email());
		credential.setHashedPassword(command.hashedPassword());
		credential.setPasswordSalt(command.passwordSalt());
		credential.setPasswordLastChangedAt(Instant.now());
		credential.setActive(true);
		return repository.save(credential);
	}
}
