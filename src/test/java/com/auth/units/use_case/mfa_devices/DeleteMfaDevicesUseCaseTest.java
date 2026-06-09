package com.auth.units.use_case.mfa_devices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.mfa_devices.DeleteMfaDevicesUseCase;
import com.auth.domain.entities.MfaDevice;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort.DeleteMfaDevicesCommand;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteMfaDevicesUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";

	@Mock
	private MfaDevicesRepositoryPort repository;

	private DeleteMfaDevicesUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new DeleteMfaDevicesUseCase(repository);
	}

	@Test
	void deletesWhenPresent() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(new MfaDevice()));
		useCase.delete(new DeleteMfaDevicesCommand(PUBLIC_ID));
		verify(repository).delete(PUBLIC_ID);
	}

	@Test
	void notFound() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
		BusinessError ex =
				assertThrows(BusinessError.class, () -> useCase.delete(new DeleteMfaDevicesCommand(PUBLIC_ID)));
		assertThat(ex.getCode()).isEqualTo(CodesError.MFA_DEVICES_NOT_FOUND);
		verify(repository, never()).delete(any());
	}
}
