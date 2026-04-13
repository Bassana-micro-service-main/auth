package com.auth.application.mappers.credentials;

import com.auth.application.dto.credentials.CreateCredentialsDto;
import com.auth.application.dto.credentials.CredentialsResponseDto;
import com.auth.application.dto.credentials.GetCredentialsDto;
import com.auth.application.dto.credentials.ListCredentialsDto;
import com.auth.application.dto.credentials.UpdateCredentialsBodyDto;
import com.auth.application.dto.credentials.UpdateCredentialsDto;
import com.auth.domain.entities.Credential;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort.CreateCredentialsCommand;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByEmailQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort.UpdateCredentialsCommand;

/**
 * Mapper HTTP -> contrats du domaine (ports in) pour credentials.
 */
public final class CredentialsHttpsMapper {

	private CredentialsHttpsMapper() {
	}

	public static CreateCredentialsCommand toCreateCommand(CreateCredentialsDto dto) {
		return new CreateCredentialsCommand(
				dto.userId(),
				dto.email(),
				dto.hashedPassword(),
				dto.passwordSalt());
	}

	public static UpdateCredentialsCommand toUpdateCommand(UpdateCredentialsDto dto) {
		return new UpdateCredentialsCommand(
				dto.publicId(),
				dto.email(),
				dto.hashedPassword(),
				dto.passwordSalt(),
				dto.active());
	}

	public static UpdateCredentialsCommand toUpdateCommand(String publicId, UpdateCredentialsBodyDto dto) {
		return new UpdateCredentialsCommand(
				publicId,
				dto.email(),
				dto.hashedPassword(),
				dto.passwordSalt(),
				dto.active());
	}

	public static CredentialsResponseDto toResponse(Credential credential) {
		if (credential == null) {
			return null;
		}
		return new CredentialsResponseDto(
				credential.getPublicId(),
				credential.getUserId(),
				credential.getEmail(),
				credential.isActive(),
				credential.getPasswordLastChangedAt(),
				credential.getCreatedAt(),
				credential.getUpdatedAt());
	}

	public static FindByPublicIdQuery toGetQuery(GetCredentialsDto dto) {
		return new FindByPublicIdQuery(dto.publicId());
	}

	public static FindByEmailQuery toListQuery(ListCredentialsDto.ByEmail dto) {
		return new FindByEmailQuery(dto.email());
	}

	public static FindByUserIdQuery toListQuery(ListCredentialsDto.ByUserId dto) {
		return new FindByUserIdQuery(dto.userId());
	}
}
