package com.auth.adapter.in.mfa_devices;

import com.auth.adapter.out.persistence.MfaDevicesRepositoryAdapter;
import com.auth.application.use_case.mfa_devices.CreateMfaDevicesUseCase;
import com.auth.application.use_case.mfa_devices.DeleteMfaDevicesUseCase;
import com.auth.application.use_case.mfa_devices.GetMfaDevicesUseCase;
import com.auth.application.use_case.mfa_devices.UpdateMfaDevicesUseCase;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import com.auth.infrastructure.database.hibernate.repository.MfaDeviceEntityRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Module MFA (équivalent NestJS {@code @Module}).
 */
@Configuration
@Import(MfaDevicesControllerAdapter.class)
public class MfaDevicesModule {

	@Bean(name = MfaDevicesRepositoryPort.REPOSITORY_QUALIFIER)
	public MfaDevicesRepositoryPort mfaDevicesRepositoryPort(MfaDeviceEntityRepository jpa) {
		return new MfaDevicesRepositoryAdapter(jpa);
	}

	@Bean
	public CreateMfaDevicesInterfacePort createMfaDevicesUseCase(
			@Qualifier(MfaDevicesRepositoryPort.REPOSITORY_QUALIFIER) MfaDevicesRepositoryPort repository) {
		return new CreateMfaDevicesUseCase(repository);
	}

	@Bean
	public GetMfaDevicesInterfacePort getMfaDevicesUseCase(
			@Qualifier(MfaDevicesRepositoryPort.REPOSITORY_QUALIFIER) MfaDevicesRepositoryPort repository) {
		return new GetMfaDevicesUseCase(repository);
	}

	@Bean
	public UpdateMfaDevicesInterfacePort updateMfaDevicesUseCase(
			@Qualifier(MfaDevicesRepositoryPort.REPOSITORY_QUALIFIER) MfaDevicesRepositoryPort repository) {
		return new UpdateMfaDevicesUseCase(repository);
	}

	@Bean
	public DeleteMfaDevicesInterfacePort deleteMfaDevicesUseCase(
			@Qualifier(MfaDevicesRepositoryPort.REPOSITORY_QUALIFIER) MfaDevicesRepositoryPort repository) {
		return new DeleteMfaDevicesUseCase(repository);
	}
}
