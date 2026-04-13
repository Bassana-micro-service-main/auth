package com.auth.application.use_case.mfa_devices;

import com.auth.domain.entities.MfaDevice;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort.CreateMfaDevicesCommand;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import com.auth.domain.services.validators.mfa_devices.CreateMfaDevicesValidators;
import com.auth.lib.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Création d’un périphérique MFA.
 */
@Transactional
public class CreateMfaDevicesUseCase implements CreateMfaDevicesInterfacePort {

	private final MfaDevicesRepositoryPort repository;

	public CreateMfaDevicesUseCase(
			@Qualifier(MfaDevicesRepositoryPort.REPOSITORY_QUALIFIER) MfaDevicesRepositoryPort repository) {
		this.repository = repository;
	}

	@Override
	public MfaDevice create(CreateMfaDevicesCommand command) {
		CreateMfaDevicesValidators.validate(command);
		MfaDevice device = new MfaDevice();
		device.setPublicId(Utils.newNanoid());
		device.setUserId(command.userId());
		device.setType(command.type());
		device.setSecret(command.secret());
		device.setPhoneNumber(command.phoneNumber());
		device.setDeviceName(command.deviceName());
		device.setActive(command.active());
		return repository.save(device);
	}
}
