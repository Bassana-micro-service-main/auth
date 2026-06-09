package com.auth.units.use_case.mfa_devices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.mfa_devices.GetMfaDevicesUseCase;
import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdAndTypeQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetMfaDevicesUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Mock
	private MfaDevicesRepositoryPort repository;

	private GetMfaDevicesUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new GetMfaDevicesUseCase(repository);
	}

	@Test
	void findByPublicId() {
		var d = new MfaDevice();
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(d));
		assertThat(useCase.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).contains(d);
	}

	@Test
	void findByUserId() {
		var d = new MfaDevice();
		when(repository.findByUserId(USER_ID)).thenReturn(List.of(d));
		assertThat(useCase.findByUserId(new FindByUserIdQuery(USER_ID))).containsExactly(d);
	}

	@Test
	void findByUserIdAndType() {
		var d = new MfaDevice();
		when(repository.findByUserIdAndType(USER_ID, MfaType.TOTP)).thenReturn(Optional.of(d));
		assertThat(useCase.findByUserIdAndType(new FindByUserIdAndTypeQuery(USER_ID, MfaType.TOTP)))
				.contains(d);
	}

	@Test
	void invalidPublicId() {
		assertThrows(BusinessError.class, () -> useCase.findByPublicId(new FindByPublicIdQuery("x")));
		verify(repository, never()).findByPublicId(any());
	}

	@Test
	void nullTypeForUserIdAndType() {
		BusinessError ex = assertThrows(
				BusinessError.class, () -> useCase.findByUserIdAndType(new FindByUserIdAndTypeQuery(USER_ID, null)));
		assertThat(ex.getCode()).isEqualTo(CodesError.MFA_DEVICES_TYPE_INVALID);
	}
}
