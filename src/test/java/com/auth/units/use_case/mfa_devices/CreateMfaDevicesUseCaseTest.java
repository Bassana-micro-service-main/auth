package com.auth.units.use_case.mfa_devices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.mfa_devices.CreateMfaDevicesUseCase;
import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort.CreateMfaDevicesCommand;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import com.auth.lib.Utils;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateMfaDevicesUseCaseTest {

	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Mock
	private MfaDevicesRepositoryPort repository;

	private CreateMfaDevicesUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new CreateMfaDevicesUseCase(repository);
	}

	@Test
	void shouldCreate() {
		var cmd = new CreateMfaDevicesCommand(USER_ID, MfaType.TOTP, "secret", null, "App", true);
		when(repository.save(any(MfaDevice.class))).thenAnswer(inv -> inv.getArgument(0));

		MfaDevice result = useCase.create(cmd);

		ArgumentCaptor<MfaDevice> captor = ArgumentCaptor.forClass(MfaDevice.class);
		verify(repository).save(captor.capture());
		assertThat(captor.getValue().getPublicId()).matches(Utils.NANOID_REGEX.pattern());
		assertThat(captor.getValue().getType()).isEqualTo(MfaType.TOTP);
		assertThat(result).isSameAs(captor.getValue());
	}

	@Test
	void validationBlocksSave() {
		assertThrows(BusinessError.class, () -> useCase.create(
				new CreateMfaDevicesCommand(null, MfaType.TOTP, "s", null, "d", true)));
		verify(repository, never()).save(any());
	}
}
