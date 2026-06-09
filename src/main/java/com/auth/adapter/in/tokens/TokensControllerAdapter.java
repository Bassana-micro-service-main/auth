package com.auth.adapter.in.tokens;

import com.auth.application.dto.tokens.CreateTokensDto;
import com.auth.application.dto.tokens.GetTokensDto;
import com.auth.application.dto.tokens.ListTokensDto;
import com.auth.application.dto.tokens.TokensResponseDto;
import com.auth.application.dto.tokens.UpdateTokensBodyDto;
import com.auth.application.mappers.tokens.TokensHttpsMapper;
import com.auth.domain.enums.TokenType;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort;
import com.auth.domain.ports.in.tokens.DeleteTokensInterfacePort;
import com.auth.domain.ports.in.tokens.DeleteTokensInterfacePort.DeleteTokensCommand;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort;
import java.util.List;
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
 * Adaptateur HTTP pour les jetons.
 */
@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
public class TokensControllerAdapter {

	private final CreateTokensInterfacePort createTokens;
	private final GetTokensInterfacePort getTokens;
	private final UpdateTokensInterfacePort updateTokens;
	private final DeleteTokensInterfacePort deleteTokens;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TokensResponseDto create(@RequestBody CreateTokensDto dto) {
		var created = createTokens.create(TokensHttpsMapper.toCreateCommand(dto));
		return TokensHttpsMapper.toResponse(created);
	}

	@GetMapping("/{publicId}")
	public TokensResponseDto get(@PathVariable String publicId) {
		return getTokens
				.findByPublicId(TokensHttpsMapper.toGetQuery(new GetTokensDto(publicId)))
				.map(TokensHttpsMapper::toResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping
	public List<TokensResponseDto> list(
			@RequestParam(required = false) TokenType type,
			@RequestParam(required = false) String value) {
		if (type != null && value != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specify only one of type or value");
		}
		if (type != null) {
			return getTokens.findByType(TokensHttpsMapper.toListQuery(new ListTokensDto.ByType(type))).stream()
					.map(TokensHttpsMapper::toResponse)
					.toList();
		}
		if (value != null) {
			return getTokens.findByValue(TokensHttpsMapper.toListQuery(new ListTokensDto.ByValue(value)))
					.stream()
					.map(TokensHttpsMapper::toResponse)
					.toList();
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query parameter type or value is required");
	}

	@PatchMapping("/{publicId}")
	public TokensResponseDto update(
			@PathVariable String publicId, @RequestBody UpdateTokensBodyDto dto) {
		var updated = updateTokens.update(TokensHttpsMapper.toUpdateCommand(publicId, dto));
		return TokensHttpsMapper.toResponse(updated);
	}

	@DeleteMapping("/{publicId}")
	public ResponseEntity<ApiMessageResponse> delete(@PathVariable String publicId) {
		deleteTokens.delete(new DeleteTokensCommand(publicId));
		return ResponseEntity.ok(new ApiMessageResponse("Token deleted successfully"));
	}
}
