package com.auth.application.mappers.mfa_devices;

import com.auth.application.dto.mfa_devices.CreateMfaDevicesDto;
import com.auth.application.dto.mfa_devices.GetMfaDevicesDto;
import com.auth.application.dto.mfa_devices.ListMfaDevicesDto;
import com.auth.application.dto.mfa_devices.UpdateMfaDevicesDto;
import com.auth.domain.ports.in.mfa_devices.CreateMfaDevicesInterfacePort.CreateMfaDevicesCommand;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdAndTypeQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.in.mfa_devices.UpdateMfaDevicesInterfacePort.UpdateMfaDevicesCommand;

/**
 * Mapper HTTP -> contrats du domaine (ports in) pour périphériques MFA.
 */
public final class MfaDevicesHttpsMapper {

	private MfaDevicesHttpsMapper() {
	}

	public static CreateMfaDevicesCommand toCreateCommand(CreateMfaDevicesDto dto) {
		return new CreateMfaDevicesCommand(
				dto.userId(),
				dto.type(),
				dto.secret(),
				dto.phoneNumber(),
				dto.deviceName(),
				dto.active());
	}

	public static UpdateMfaDevicesCommand toUpdateCommand(UpdateMfaDevicesDto dto) {
		return new UpdateMfaDevicesCommand(
				dto.publicId(),
				dto.type(),
				dto.secret(),
				dto.phoneNumber(),
				dto.deviceName(),
				dto.active(),
				dto.lastUsedAt());
	}

	public static FindByPublicIdQuery toGetQuery(GetMfaDevicesDto dto) {
		return new FindByPublicIdQuery(dto.publicId());
	}

	public static FindByUserIdQuery toListQuery(ListMfaDevicesDto.ByUserId dto) {
		return new FindByUserIdQuery(dto.userId());
	}

	public static FindByUserIdAndTypeQuery toListQuery(ListMfaDevicesDto.ByUserIdAndType dto) {
		return new FindByUserIdAndTypeQuery(dto.userId(), dto.type());
	}
}
