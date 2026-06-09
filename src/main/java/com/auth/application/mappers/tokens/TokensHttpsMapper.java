package com.auth.application.mappers.tokens;

import com.auth.application.dto.tokens.CreateTokensDto;
import com.auth.application.dto.tokens.GetTokensDto;
import com.auth.application.dto.tokens.ListTokensDto;
import com.auth.application.dto.tokens.TokensResponseDto;
import com.auth.application.dto.tokens.UpdateTokensBodyDto;
import com.auth.application.dto.tokens.UpdateTokensDto;
import com.auth.domain.entities.Token;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort.CreateTokensCommand;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByTypeQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByValueQuery;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort.UpdateTokensCommand;

/**
 * Mapper HTTP -> contrats du domaine (ports in) pour jetons.
 */
public final class TokensHttpsMapper {

	private TokensHttpsMapper() {
	}

	public static CreateTokensCommand toCreateCommand(CreateTokensDto dto) {
		return new CreateTokensCommand(
				dto.type(),
				dto.value(),
				dto.expiresAt());
	}

	public static UpdateTokensCommand toUpdateCommand(UpdateTokensDto dto) {
		return new UpdateTokensCommand(
				dto.publicId(),
				dto.value(),
				dto.expiresAt());
	}

	public static UpdateTokensCommand toUpdateCommand(String publicId, UpdateTokensBodyDto dto) {
		return new UpdateTokensCommand(
				publicId,
				dto.value(),
				dto.expiresAt());
	}

	public static TokensResponseDto toResponse(Token token) {
		if (token == null) {
			return null;
		}
		return new TokensResponseDto(
				token.getPublicId(),
				token.getType(),
				token.getExpiresAt(),
				token.getCreatedAt());
	}

	public static FindByPublicIdQuery toGetQuery(GetTokensDto dto) {
		return new FindByPublicIdQuery(dto.publicId());
	}

	public static FindByTypeQuery toListQuery(ListTokensDto.ByType dto) {
		return new FindByTypeQuery(dto.type());
	}

	public static FindByValueQuery toListQuery(ListTokensDto.ByValue dto) {
		return new FindByValueQuery(dto.value());
	}
}
