package com.auth.adapter.in.credentials;

import com.auth.application.dto.credentials.CreateCredentialsDto;
import com.auth.application.dto.credentials.CredentialsResponseDto;
import com.auth.application.dto.credentials.GetCredentialsDto;
import com.auth.application.dto.credentials.ListCredentialsDto;
import com.auth.application.dto.credentials.UpdateCredentialsBodyDto;
import com.auth.application.mappers.credentials.CredentialsHttpsMapper;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort.DeleteCredentialsCommand;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort;
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
 * Adaptateur HTTP pour les credentials (équivalent NestJS {@code @Controller('users')} + use cases).
 */
@RestController
@RequestMapping("/credentials")
@RequiredArgsConstructor
public class CredentialsControllerAdapter {

	private final CreateCredentialsInterfacePort createCredentials;
	private final GetCredentialsInterfacePort getCredentials;
	private final UpdateCredentialsInterfacePort updateCredentials;
	private final DeleteCredentialsInterfacePort deleteCredentials;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CredentialsResponseDto create(@RequestBody CreateCredentialsDto dto) {
		var created = createCredentials.create(CredentialsHttpsMapper.toCreateCommand(dto));
		return CredentialsHttpsMapper.toResponse(created);
	}

	@GetMapping("/{publicId}")
	public CredentialsResponseDto get(@PathVariable String publicId) {
		return getCredentials
				.findByPublicId(CredentialsHttpsMapper.toGetQuery(new GetCredentialsDto(publicId)))
				.map(CredentialsHttpsMapper::toResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping
	public List<CredentialsResponseDto> list(
			@RequestParam(required = false) String email,
			@RequestParam(required = false) UUID userId) {
		if (email != null && userId != null) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Specify only one of email or userId");
		}
		if (email != null) {
			return getCredentials
					.findByEmail(CredentialsHttpsMapper.toListQuery(new ListCredentialsDto.ByEmail(email)))
					.stream()
					.map(CredentialsHttpsMapper::toResponse)
					.toList();
		}
		if (userId != null) {
			return getCredentials
					.findByUserId(CredentialsHttpsMapper.toListQuery(new ListCredentialsDto.ByUserId(userId)))
					.stream()
					.map(CredentialsHttpsMapper::toResponse)
					.toList();
		}
		throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Query parameter email or userId is required");
	}

	@PatchMapping("/{publicId}")
	public CredentialsResponseDto update(
			@PathVariable String publicId, @RequestBody UpdateCredentialsBodyDto dto) {
		var updated = updateCredentials.update(CredentialsHttpsMapper.toUpdateCommand(publicId, dto));
		return CredentialsHttpsMapper.toResponse(updated);
	}

	@DeleteMapping("/{publicId}")
	public ResponseEntity<ApiMessageResponse> delete(@PathVariable String publicId) {
		deleteCredentials.delete(new DeleteCredentialsCommand(publicId));
		return ResponseEntity.ok(new ApiMessageResponse("Credentials deleted successfully"));
	}
}
