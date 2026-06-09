package com.auth.adapter.in.credentials;

import com.auth.application.use_case.credentials.CreateCredentialsUseCase;
import com.auth.application.use_case.credentials.DeleteCredentialsUseCase;
import com.auth.application.use_case.credentials.GetCredentialsUseCase;
import com.auth.application.use_case.credentials.UpdateCredentialsUseCase;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort;
import com.auth.adapter.out.persistence.CredentialsRepositoryAdapter;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.infrastructure.database.hibernate.repository.CredentialEntityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Module credentials, aligné sur l’idée d’un {@code @Module} NestJS.
 * <ul>
 *   <li>{@code imports} — persistance JPA / Hibernate : {@link CredentialEntityRepository} (Spring Data),
 *       bootstrap global dans {@link com.auth.service.ServiceApplication} ({@code @EnableJpaRepositories}, {@code @EntityScan}).</li>
 *   <li>{@code controllers} — {@link CredentialsControllerAdapter}.</li>
 *   <li>{@code providers} — liaison explicite port sortant → adaptateur ; un bean par cas d’usage (ports entrants).</li>
 *   <li>{@code exports} — {@link CreateCredentialsInterfacePort}, {@link GetCredentialsInterfacePort},
 *       {@link UpdateCredentialsInterfacePort}, {@link DeleteCredentialsInterfacePort}.</li>
 * </ul>
 */
@Configuration
@Import(CredentialsControllerAdapter.class)
public class CredentialsModule {

	@Bean(name = CredentialsRepositoryPort.REPOSITORY_QUALIFIER)
	public CredentialsRepositoryPort credentialsRepositoryPort(CredentialEntityRepository jpa) {
		return new CredentialsRepositoryAdapter(jpa);
	}

	@Bean
	public CreateCredentialsInterfacePort createCredentialsUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort repository) {
		return new CreateCredentialsUseCase(repository);
	}

	@Bean
	public GetCredentialsInterfacePort getCredentialsUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort repository) {
		return new GetCredentialsUseCase(repository);
	}

	@Bean
	public UpdateCredentialsInterfacePort updateCredentialsUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort repository) {
		return new UpdateCredentialsUseCase(repository);
	}

	@Bean
	public DeleteCredentialsInterfacePort deleteCredentialsUseCase(
			@Qualifier(CredentialsRepositoryPort.REPOSITORY_QUALIFIER) CredentialsRepositoryPort repository) {
		return new DeleteCredentialsUseCase(repository);
	}
}
