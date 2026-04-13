package com.auth.application.use_case.mfa_devices;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort.DeleteMfaDevicesCommand;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import com.auth.domain.services.validators.mfa_devices.DeleteMfaDevicesValidators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Suppression d’un périphérique MFA.
 */
@Transactional
public class DeleteMfaDevicesUseCase implements DeleteMfaDevicesInterfacePort {

	private final MfaDevicesRepositoryPort repository;

	public DeleteMfaDevicesUseCase(
			@Qualifier(MfaDevicesRepositoryPort.REPOSITORY_QUALIFIER) MfaDevicesRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public void delete(DeleteMfaDevicesCommand command) {
		DeleteMfaDevicesValidators.validate(command);
		if (repository.findByPublicId(command.publicId()).isEmpty()) {
			throw new BusinessError(CodesError.MFA_DEVICES_NOT_FOUND);
		}
		repository.delete(command.publicId());
	}
}
