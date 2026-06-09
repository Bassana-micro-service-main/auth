package com.auth.application.use_case.credentials;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort.DeleteCredentialsCommand;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.domain.services.validators.credentials.DeleteCredentialsValidators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Suppression des identifiants par {@code publicId}.
 */
@Transactional
public class DeleteCredentialsUseCase implements DeleteCredentialsInterfacePort {

	private final CredentialsRepositoryPort repository;

	public DeleteCredentialsUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public void delete(DeleteCredentialsCommand command) {
		DeleteCredentialsValidators.validate(command);
		if (repository.findByPublicId(command.publicId()).isEmpty()) {
			throw new BusinessError(CodesError.CREDENTIALS_NOT_FOUND);
		}
		repository.delete(command.publicId());
	}
}
