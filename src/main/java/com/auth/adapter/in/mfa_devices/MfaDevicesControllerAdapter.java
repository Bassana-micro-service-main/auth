package com.auth.adapter.in.mfa_devices;

import com.auth.application.dto.mfa_devices.CreateMfaDevicesDto;
import com.auth.application.dto.mfa_devices.GetMfaDevicesDto;
import com.auth.application.dto.mfa_devices.ListMfaDevicesDto;
import com.auth.application.dto.mfa_devices.MfaDevicesResponseDto;
import com.auth.application.dto.mfa_devices.UpdateMfaDevicesBodyDto;
import com.auth.application.mappers.mfa_devices.MfaDevicesHttpsMapper;
import com.auth.domain.enums.MfaType;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.DeleteMfaDevicesInterfacePort.DeleteMfaDevicesCommand;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Adaptateur HTTP pour les périphériques MFA.
 */
@RestController
@RequestMapping("/mfa-devices")
@RequiredArgsConstructor
public class MfaDevicesControllerAdapter {

	private final CreateMfaDevicesInterfacePort createMfaDevices;
	private final GetMfaDevicesInterfacePort getMfaDevices;
	private final UpdateMfaDevicesInterfacePort updateMfaDevices;
	private final DeleteMfaDevicesInterfacePort deleteMfaDevices;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MfaDevicesResponseDto create(@RequestBody CreateMfaDevicesDto dto) {
		var created = createMfaDevices.create(MfaDevicesHttpsMapper.toCreateCommand(dto));
		return MfaDevicesHttpsMapper.toResponse(created);
	}

	@GetMapping("/{publicId}")
	public MfaDevicesResponseDto get(@PathVariable String publicId) {
		return getMfaDevices
				.findByPublicId(MfaDevicesHttpsMapper.toGetQuery(new GetMfaDevicesDto(publicId)))
				.map(MfaDevicesHttpsMapper::toResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping
	public List<MfaDevicesResponseDto> list(
			@RequestParam UUID userId, @RequestParam(required = false) MfaType type) {
		if (type != null) {
			return getMfaDevices
					.findByUserIdAndType(
							MfaDevicesHttpsMapper.toListQuery(new ListMfaDevicesDto.ByUserIdAndType(userId, type)))
					.stream()
					.map(MfaDevicesHttpsMapper::toResponse)
					.toList();
		}
		return getMfaDevices.findByUserId(MfaDevicesHttpsMapper.toListQuery(new ListMfaDevicesDto.ByUserId(userId)))
				.stream()
				.map(MfaDevicesHttpsMapper::toResponse)
				.toList();
	}

	@PatchMapping("/{publicId}")
	public MfaDevicesResponseDto update(
			@PathVariable String publicId, @RequestBody UpdateMfaDevicesBodyDto dto) {
		var updated = updateMfaDevices.update(MfaDevicesHttpsMapper.toUpdateCommand(publicId, dto));
		return MfaDevicesHttpsMapper.toResponse(updated);
	}

	@DeleteMapping("/{publicId}")
	public ResponseEntity<ApiMessageResponse> delete(@PathVariable String publicId) {
		deleteMfaDevices.delete(new DeleteMfaDevicesCommand(publicId));
		return ResponseEntity.ok(new ApiMessageResponse("MFA device deleted successfully"));
	}
}
