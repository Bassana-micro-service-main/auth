package com.auth.application.use_case.mfa_devices;

import com.auth.domain.entities.MfaDevice;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdAndTypeQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import com.auth.domain.services.validators.mfa_devices.GetMfaDevicesQueriesValidators;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultation des périphériques MFA.
 */
@Transactional(readOnly = true)
public class GetMfaDevicesUseCase implements GetMfaDevicesInterfacePort {

	private final MfaDevicesRepositoryPort repository;

	public GetMfaDevicesUseCase(
			@Qualifier(MfaDevicesRepositoryPort.REPOSITORY_QUALIFIER) MfaDevicesRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public Optional<MfaDevice> findByPublicId(FindByPublicIdQuery query) {
		GetMfaDevicesQueriesValidators.validate(query);
		return repository.findByPublicId(query.publicId());
	}

	@Override
	public List<MfaDevice> findByUserId(FindByUserIdQuery query) {
		GetMfaDevicesQueriesValidators.validate(query);
		return repository.findByUserId(query.userId());
	}

	@Override
	public Optional<MfaDevice> findByUserIdAndType(FindByUserIdAndTypeQuery query) {
		GetMfaDevicesQueriesValidators.validate(query);
		return repository.findByUserIdAndType(query.userId(), query.type());
	}
}
