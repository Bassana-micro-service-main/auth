package com.auth.application.use_case.credentials;

import com.auth.domain.entities.Credential;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort.UpdateCredentialsCommand;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.domain.services.validators.credentials.UpdateCredentialsValidators;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mise à jour des identifiants (champs optionnels).
 */
@Transactional
public class UpdateCredentialsUseCase implements UpdateCredentialsInterfacePort {

	private final CredentialsRepositoryPort repository;

	public UpdateCredentialsUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Credential update(UpdateCredentialsCommand command) {
		UpdateCredentialsValidators.validate(command);
		Credential credential = repository
				.findByPublicId(command.publicId())
				.orElseThrow(() -> new BusinessError(CodesError.CREDENTIALS_NOT_FOUND));
		command.email().ifPresent(credential::setEmail);
		command.hashedPassword().ifPresent(hash -> {
			credential.setHashedPassword(hash);
			credential.setPasswordLastChangedAt(Instant.now());
		});
		command.passwordSalt().ifPresent(credential::setPasswordSalt);
		command.active().ifPresent(credential::setActive);
		return repository.save(credential);
	}
}
