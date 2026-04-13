package com.auth.units.use_case.mfa_devices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.mfa_devices.UpdateMfaDevicesUseCase;
import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort.UpdateMfaDevicesCommand;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateMfaDevicesUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";

	@Mock
	private MfaDevicesRepositoryPort repository;

	private UpdateMfaDevicesUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new UpdateMfaDevicesUseCase(repository);
	}

	private MfaDevice existing() {
		var d = new MfaDevice();
		d.setPublicId(PUBLIC_ID);
		d.setType(MfaType.TOTP);
		return d;
	}

	@Test
	void shouldUpdate() {
		var dev = existing();
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(dev));
		when(repository.save(any(MfaDevice.class))).thenAnswer(inv -> inv.getArgument(0));

		var cmd = new UpdateMfaDevicesCommand(
				PUBLIC_ID,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.of("new-name"),
				Optional.empty(),
				Optional.empty());

		assertThat(useCase.update(cmd).getDeviceName()).isEqualTo("new-name");
	}

	@Test
	void notFound() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
		var cmd = new UpdateMfaDevicesCommand(
				PUBLIC_ID,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty());
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.update(cmd));
		assertThat(ex.getCode()).isEqualTo(CodesError.MFA_DEVICES_NOT_FOUND);
	}
}
