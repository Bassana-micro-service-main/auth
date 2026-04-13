package com.auth.units.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.adapter.in.mfa_devices.ApiMessageResponse;
import com.auth.adapter.in.mfa_devices.MfaDevicesControllerAdapter;
import com.auth.application.dto.mfa_devices.CreateMfaDevicesDto;
import com.auth.application.dto.mfa_devices.MfaDevicesResponseDto;
import com.auth.application.dto.mfa_devices.UpdateMfaDevicesBodyDto;
import com.auth.application.mappers.mfa_devices.MfaDevicesHttpsMapper;
import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort.CreateMfaDevicesCommand;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort.DeleteMfaDevicesCommand;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdAndTypeQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort.UpdateMfaDevicesCommand;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
@DisplayName("MfaDevicesControllerAdapter")
class MfaDevicesControllerTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";

	@Mock
	private CreateMfaDevicesInterfacePort createMfaDevices;

	@Mock
	private GetMfaDevicesInterfacePort getMfaDevices;

	@Mock
	private UpdateMfaDevicesInterfacePort updateMfaDevices;

	@Mock
	private DeleteMfaDevicesInterfacePort deleteMfaDevices;

	private MfaDevicesControllerAdapter controller;

	@BeforeEach
	void setUp() {
		controller = new MfaDevicesControllerAdapter(
				createMfaDevices, getMfaDevices, updateMfaDevices, deleteMfaDevices);
	}

	private static MfaDevice sampleDevice() {
		var d = new MfaDevice();
		d.setPublicId(PUBLIC_ID);
		d.setUserId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
		d.setType(MfaType.TOTP);
		d.setSecret("secret");
		d.setPhoneNumber(null);
		d.setDeviceName("Auth app");
		d.setActive(true);
		d.setLastUsedAt(null);
		d.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
		return d;
	}

	@Nested
	@DisplayName("POST /mfa-devices")
	class Create {

		@Test
		void shouldCreate() {
			var dto = new CreateMfaDevicesDto(
					UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
					MfaType.TOTP,
					"shared-secret",
					null,
					"Authenticator",
					true);
			var created = sampleDevice();
			when(createMfaDevices.create(MfaDevicesHttpsMapper.toCreateCommand(dto))).thenReturn(created);

			MfaDevicesResponseDto result = controller.create(dto);

			ArgumentCaptor<CreateMfaDevicesCommand> captor = ArgumentCaptor.forClass(CreateMfaDevicesCommand.class);
			verify(createMfaDevices).create(captor.capture());
			assertThat(captor.getValue()).isEqualTo(MfaDevicesHttpsMapper.toCreateCommand(dto));
			assertThat(result).isEqualTo(MfaDevicesHttpsMapper.toResponse(created));
		}
	}

	@Nested
	@DisplayName("GET /mfa-devices/{publicId}")
	class Get {

		@Test
		void shouldGet() {
			var found = sampleDevice();
			when(getMfaDevices.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).thenReturn(Optional.of(found));

			MfaDevicesResponseDto result = controller.get(PUBLIC_ID);

			verify(getMfaDevices).findByPublicId(argThat(q -> q.publicId().equals(PUBLIC_ID)));
			assertThat(result).isEqualTo(MfaDevicesHttpsMapper.toResponse(found));
		}

		@Test
		void should404() {
			when(getMfaDevices.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).thenReturn(Optional.empty());

			assertThatThrownBy(() -> controller.get(PUBLIC_ID))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
		}
	}

	@Nested
	@DisplayName("GET /mfa-devices (list)")
	class ListEndpoints {

		@Test
		void shouldListByUserId() {
			UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
			var dev = sampleDevice();
			when(getMfaDevices.findByUserId(new FindByUserIdQuery(userId))).thenReturn(List.of(dev));

			List<MfaDevicesResponseDto> result = controller.list(userId, null);

			verify(getMfaDevices).findByUserId(argThat(q -> q.userId().equals(userId)));
			assertThat(result).containsExactly(MfaDevicesHttpsMapper.toResponse(dev));
		}

		@Test
		void shouldListByUserIdAndType() {
			UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
			var dev = sampleDevice();
			when(getMfaDevices.findByUserIdAndType(new FindByUserIdAndTypeQuery(userId, MfaType.TOTP)))
					.thenReturn(Optional.of(dev));

			List<MfaDevicesResponseDto> result = controller.list(userId, MfaType.TOTP);

			verify(getMfaDevices).findByUserIdAndType(argThat(q -> q.userId().equals(userId) && q.type() == MfaType.TOTP));
			assertThat(result).containsExactly(MfaDevicesHttpsMapper.toResponse(dev));
		}
	}

	@Nested
	@DisplayName("PATCH /mfa-devices/{publicId}")
	class Update {

		@Test
		void shouldUpdate() {
			var body = new UpdateMfaDevicesBodyDto(
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.of("New name"),
					Optional.empty(),
					Optional.empty());
			var updated = sampleDevice();
			updated.setDeviceName("New name");
			UpdateMfaDevicesCommand expected = MfaDevicesHttpsMapper.toUpdateCommand(PUBLIC_ID, body);
			when(updateMfaDevices.update(expected)).thenReturn(updated);

			MfaDevicesResponseDto result = controller.update(PUBLIC_ID, body);

			verify(updateMfaDevices).update(expected);
			assertThat(result).isEqualTo(MfaDevicesHttpsMapper.toResponse(updated));
		}
	}

	@Nested
	@DisplayName("DELETE /mfa-devices/{publicId}")
	class Delete {

		@Test
		void shouldDelete() {
			ResponseEntity<ApiMessageResponse> result = controller.delete(PUBLIC_ID);

			verify(deleteMfaDevices).delete(new DeleteMfaDevicesCommand(PUBLIC_ID));
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(result.getBody().message()).isEqualTo("MFA device deleted successfully");
		}
	}
}
