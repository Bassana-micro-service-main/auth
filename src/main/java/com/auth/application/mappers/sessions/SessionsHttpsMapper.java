package com.auth.application.mappers.sessions;

import com.auth.application.dto.sessions.CreateSessionsDto;
import com.auth.application.dto.sessions.GetSessionsDto;
import com.auth.application.dto.sessions.ListSessionsDto;
import com.auth.application.dto.sessions.SessionsResponseDto;
import com.auth.application.dto.sessions.UpdateSessionsBodyDto;
import com.auth.application.dto.sessions.UpdateSessionsDto;
import com.auth.domain.entities.Session;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort.CreateSessionsCommand;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByRefreshTokenQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort.UpdateSessionsCommand;

/**
 * Mapper HTTP -> contrats du domaine (ports in) pour sessions.
 */
public final class SessionsHttpsMapper {

	private SessionsHttpsMapper() {
	}

	public static CreateSessionsCommand toCreateCommand(CreateSessionsDto dto) {
		return new CreateSessionsCommand(
				dto.userId(),
				dto.ipAddress(),
				dto.userAgent(),
				dto.deviceName(),
				dto.refreshToken(),
				dto.expiresAt());
	}

	public static UpdateSessionsCommand toUpdateCommand(UpdateSessionsDto dto) {
		return new UpdateSessionsCommand(
				dto.publicId(),
				dto.ipAddress(),
				dto.userAgent(),
				dto.deviceName(),
				dto.refreshToken(),
				dto.expiresAt(),
				dto.revoked());
	}

	public static UpdateSessionsCommand toUpdateCommand(String publicId, UpdateSessionsBodyDto dto) {
		return new UpdateSessionsCommand(
				publicId,
				dto.ipAddress(),
				dto.userAgent(),
				dto.deviceName(),
				dto.refreshToken(),
				dto.expiresAt(),
				dto.revoked());
	}

	public static SessionsResponseDto toResponse(Session session) {
		if (session == null) {
			return null;
		}
		return new SessionsResponseDto(
				session.getPublicId(),
				session.getUserId(),
				session.getIpAddress(),
				session.getUserAgent(),
				session.getDeviceName(),
				session.getExpiresAt(),
				session.isRevoked(),
				session.getCreatedAt(),
				session.getUpdatedAt());
	}

	public static FindByPublicIdQuery toGetQuery(GetSessionsDto dto) {
		return new FindByPublicIdQuery(dto.publicId());
	}

	public static FindByUserIdQuery toListQuery(ListSessionsDto.ByUserId dto) {
		return new FindByUserIdQuery(dto.userId());
	}

	public static FindByRefreshTokenQuery toListQuery(ListSessionsDto.ByRefreshToken dto) {
		return new FindByRefreshTokenQuery(dto.refreshToken());
	}
}
