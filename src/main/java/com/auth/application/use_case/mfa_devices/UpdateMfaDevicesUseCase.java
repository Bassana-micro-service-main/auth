package com.auth.application.use_case.mfa_devices;

import com.auth.domain.entities.MfaDevice;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort.UpdateMfaDevicesCommand;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import com.auth.domain.services.validators.mfa_devices.UpdateMfaDevicesValidators;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mise à jour d’un périphérique MFA.
 */
@Transactional
public class UpdateMfaDevicesUseCase implements UpdateMfaDevicesInterfacePort {

	private final MfaDevicesRepositoryPort repository;

	public UpdateMfaDevicesUseCase(
			@Qualifier(MfaDevicesRepositoryPort.REPOSITORY_QUALIFIER) MfaDevicesRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public MfaDevice update(UpdateMfaDevicesCommand command) {
		UpdateMfaDevicesValidators.validate(command);
		MfaDevice device = repository
				.findByPublicId(command.publicId())
				.orElseThrow(() -> new BusinessError(CodesError.MFA_DEVICES_NOT_FOUND));
		command.type().ifPresent(device::setType);
		command.secret().ifPresent(device::setSecret);
		command.phoneNumber().ifPresent(device::setPhoneNumber);
		command.deviceName().ifPresent(device::setDeviceName);
		command.active().ifPresent(device::setActive);
		command.lastUsedAt().ifPresent(device::setLastUsedAt);
		return repository.save(device);
	}
}
