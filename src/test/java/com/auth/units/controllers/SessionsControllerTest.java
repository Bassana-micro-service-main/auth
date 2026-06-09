package com.auth.units.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.adapter.in.sessions.ApiMessageResponse;
import com.auth.adapter.in.sessions.SessionsControllerAdapter;
import com.auth.application.dto.sessions.CreateSessionsDto;
import com.auth.application.dto.sessions.SessionsResponseDto;
import com.auth.application.dto.sessions.UpdateSessionsBodyDto;
import com.auth.application.mappers.sessions.SessionsHttpsMapper;
import com.auth.domain.entities.Session;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.CreateSessionsInterfacePort.CreateSessionsCommand;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.DeleteSessionsInterfacePort.DeleteSessionsCommand;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByRefreshTokenQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort;
import com.auth.domain.ports.in.sessions.UpdateSessionsInterfacePort.UpdateSessionsCommand;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
@DisplayName("SessionsControllerAdapter")
class SessionsControllerTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final Instant FUTURE = Instant.now().plus(7, ChronoUnit.DAYS);

	@Mock
	private CreateSessionsInterfacePort createSessions;

	@Mock
	private GetSessionsInterfacePort getSessions;

	@Mock
	private UpdateSessionsInterfacePort updateSessions;

	@Mock
	private DeleteSessionsInterfacePort deleteSessions;

	private SessionsControllerAdapter controller;

	@BeforeEach
	void setUp() {
		controller = new SessionsControllerAdapter(createSessions, getSessions, updateSessions, deleteSessions);
	}

	private static Session sampleSession() {
		var s = new Session();
		s.setPublicId(PUBLIC_ID);
		s.setUserId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
		s.setIpAddress("10.0.0.1");
		s.setUserAgent("UA");
		s.setDeviceName("Phone");
		s.setRefreshToken("rt");
		s.setExpiresAt(FUTURE);
		s.setRevoked(false);
		s.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
		s.setUpdatedAt(Instant.parse("2026-01-02T00:00:00Z"));
		return s;
	}

	@Nested
	@DisplayName("POST /sessions")
	class Create {

		@Test
		void shouldCreate() {
			var dto = new CreateSessionsDto(
					UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
					"10.0.0.1",
					"Mozilla",
					"Chrome",
					"refresh-token",
					FUTURE);
			var created = sampleSession();
			when(createSessions.create(SessionsHttpsMapper.toCreateCommand(dto))).thenReturn(created);

			SessionsResponseDto result = controller.create(dto);

			ArgumentCaptor<CreateSessionsCommand> captor = ArgumentCaptor.forClass(CreateSessionsCommand.class);
			verify(createSessions).create(captor.capture());
			assertThat(captor.getValue()).isEqualTo(SessionsHttpsMapper.toCreateCommand(dto));
			assertThat(result).isEqualTo(SessionsHttpsMapper.toResponse(created));
		}
	}

	@Nested
	@DisplayName("GET /sessions/{publicId}")
	class Get {

		@Test
		void shouldGet() {
			var found = sampleSession();
			when(getSessions.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).thenReturn(Optional.of(found));

			SessionsResponseDto result = controller.get(PUBLIC_ID);

			verify(getSessions).findByPublicId(argThat(q -> q.publicId().equals(PUBLIC_ID)));
			assertThat(result).isEqualTo(SessionsHttpsMapper.toResponse(found));
		}

		@Test
		void should404() {
			when(getSessions.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).thenReturn(Optional.empty());

			assertThatThrownBy(() -> controller.get(PUBLIC_ID))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
		}
	}

	@Nested
	@DisplayName("GET /sessions (list)")
	class ListEndpoints {

		@Test
		void shouldListByUserId() {
			UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
			var sess = sampleSession();
			when(getSessions.findByUserId(new FindByUserIdQuery(userId))).thenReturn(List.of(sess));

			List<SessionsResponseDto> result = controller.list(userId, null);

			verify(getSessions).findByUserId(argThat(q -> q.userId().equals(userId)));
			assertThat(result).containsExactly(SessionsHttpsMapper.toResponse(sess));
		}

		@Test
		void shouldListByRefreshToken() {
			var sess = sampleSession();
			when(getSessions.findByRefreshToken(new FindByRefreshTokenQuery("opaque-rt"))).thenReturn(Optional.of(sess));

			List<SessionsResponseDto> result = controller.list(null, "opaque-rt");

			verify(getSessions).findByRefreshToken(argThat(q -> q.refreshToken().equals("opaque-rt")));
			assertThat(result).containsExactly(SessionsHttpsMapper.toResponse(sess));
		}

		@Test
		void shouldRejectBothFilters() {
			assertThatThrownBy(() -> controller.list(UUID.randomUUID(), "rt"))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
		}

		@Test
		void shouldRequireOneFilter() {
			assertThatThrownBy(() -> controller.list(null, null))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
		}
	}

	@Nested
	@DisplayName("PATCH /sessions/{publicId}")
	class Update {

		@Test
		void shouldUpdate() {
			var body = new UpdateSessionsBodyDto(
					Optional.of("new-ip"),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty());
			var updated = sampleSession();
			UpdateSessionsCommand expected = SessionsHttpsMapper.toUpdateCommand(PUBLIC_ID, body);
			when(updateSessions.update(expected)).thenReturn(updated);

			SessionsResponseDto result = controller.update(PUBLIC_ID, body);

			verify(updateSessions).update(expected);
			assertThat(result).isEqualTo(SessionsHttpsMapper.toResponse(updated));
		}
	}

	@Nested
	@DisplayName("DELETE /sessions/{publicId}")
	class Delete {

		@Test
		void shouldDelete() {
			ResponseEntity<ApiMessageResponse> result = controller.delete(PUBLIC_ID);

			verify(deleteSessions).delete(new DeleteSessionsCommand(PUBLIC_ID));
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(result.getBody().message()).isEqualTo("Session deleted successfully");
		}
	}
}
