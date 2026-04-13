package com.auth.application.use_case.credentials;

import com.auth.domain.entities.Credential;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByEmailQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.domain.services.validators.credentials.GetCredentialsQueriesValidators;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultation des identifiants (par {@code publicId}, email ou utilisateur).
 */
@Transactional(readOnly = true)
public class GetCredentialsUseCase implements GetCredentialsInterfacePort {

	private final CredentialsRepositoryPort repository;

	public GetCredentialsUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Optional<Credential> findByPublicId(FindByPublicIdQuery query) {
		GetCredentialsQueriesValidators.validate(query);
		return repository.findByPublicId(query.publicId());
	}

	@Override
	public Optional<Credential> findByEmail(FindByEmailQuery query) {
		GetCredentialsQueriesValidators.validate(query);
		return repository.findByEmail(query.email());
	}

	@Override
	public Optional<Credential> findByUserId(FindByUserIdQuery query) {
		GetCredentialsQueriesValidators.validate(query);
		return repository.findByUserId(query.userId());
	}
}
