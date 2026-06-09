package com.auth.adapter.in.sessions;

import com.auth.application.dto.sessions.CreateSessionsDto;
import com.auth.application.dto.sessions.GetSessionsDto;
import com.auth.application.dto.sessions.ListSessionsDto;
import com.auth.application.dto.sessions.SessionsResponseDto;
import com.auth.application.dto.sessions.UpdateSessionsBodyDto;
import com.auth.application.mappers.sessions.SessionsHttpsMapper;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort.DeleteSessionsCommand;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort;
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
 * Adaptateur HTTP pour les sessions.
 */
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionsControllerAdapter {

	private final CreateSessionsInterfacePort createSessions;
	private final GetSessionsInterfacePort getSessions;
	private final UpdateSessionsInterfacePort updateSessions;
	private final DeleteSessionsInterfacePort deleteSessions;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SessionsResponseDto create(@RequestBody CreateSessionsDto dto) {
		var created = createSessions.create(SessionsHttpsMapper.toCreateCommand(dto));
		return SessionsHttpsMapper.toResponse(created);
	}

	@GetMapping("/{publicId}")
	public SessionsResponseDto get(@PathVariable String publicId) {
		return getSessions
				.findByPublicId(SessionsHttpsMapper.toGetQuery(new GetSessionsDto(publicId)))
				.map(SessionsHttpsMapper::toResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping
	public List<SessionsResponseDto> list(
			@RequestParam(required = false) UUID userId,
			@RequestParam(required = false) String refreshToken) {
		if (userId != null && refreshToken != null) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Specify only one of userId or refreshToken");
		}
		if (userId != null) {
			return getSessions.findByUserId(SessionsHttpsMapper.toListQuery(new ListSessionsDto.ByUserId(userId)))
					.stream()
					.map(SessionsHttpsMapper::toResponse)
					.toList();
		}
		if (refreshToken != null) {
			return getSessions
					.findByRefreshToken(
							SessionsHttpsMapper.toListQuery(new ListSessionsDto.ByRefreshToken(refreshToken)))
					.stream()
					.map(SessionsHttpsMapper::toResponse)
					.toList();
		}
		throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST, "Query parameter userId or refreshToken is required");
	}

	@PatchMapping("/{publicId}")
	public SessionsResponseDto update(
			@PathVariable String publicId, @RequestBody UpdateSessionsBodyDto dto) {
		var updated = updateSessions.update(SessionsHttpsMapper.toUpdateCommand(publicId, dto));
		return SessionsHttpsMapper.toResponse(updated);
	}

	@DeleteMapping("/{publicId}")
	public ResponseEntity<ApiMessageResponse> delete(@PathVariable String publicId) {
		deleteSessions.delete(new DeleteSessionsCommand(publicId));
		return ResponseEntity.ok(new ApiMessageResponse("Session deleted successfully"));
	}
}
