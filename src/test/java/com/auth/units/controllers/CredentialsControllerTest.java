package com.auth.units.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.adapter.in.credentials.ApiMessageResponse;
import com.auth.adapter.in.credentials.CredentialsControllerAdapter;
import com.auth.application.dto.credentials.CreateCredentialsDto;
import com.auth.application.dto.credentials.CredentialsResponseDto;
import com.auth.application.dto.credentials.UpdateCredentialsBodyDto;
import com.auth.application.mappers.credentials.CredentialsHttpsMapper;
import com.auth.domain.entities.Credential;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.CreateCredentialsInterfacePort.CreateCredentialsCommand;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.DeleteCredentialsInterfacePort.DeleteCredentialsCommand;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByEmailQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByUserIdQuery;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort;
import com.auth.domain.ports.in.credentials.UpdateCredentialsInterfacePort.UpdateCredentialsCommand;
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

/**
 * Tests unitaires de l’adaptateur HTTP credentials (équivalent
 * {@code user.controller.spec.ts} / Vitest : mocks des ports, vérification des appels et du mapping).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CredentialsControllerAdapter")
class CredentialsControllerTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";

	@Mock
	private CreateCredentialsInterfacePort createCredentials;

	@Mock
	private GetCredentialsInterfacePort getCredentials;

	@Mock
	private UpdateCredentialsInterfacePort updateCredentials;

	@Mock
	private DeleteCredentialsInterfacePort deleteCredentials;

	private CredentialsControllerAdapter controller;

	@BeforeEach
	void setUp() {
		controller = new CredentialsControllerAdapter(
				createCredentials, getCredentials, updateCredentials, deleteCredentials);
	}

	private static Credential sampleCredential() {
		var c = new Credential();
		c.setPublicId(PUBLIC_ID);
		c.setUserId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
		c.setEmail("test@test.com");
		c.setActive(true);
		c.setPasswordLastChangedAt(Instant.parse("2025-01-01T00:00:00Z"));
		c.setCreatedAt(Instant.parse("2025-01-01T00:00:00Z"));
		c.setUpdatedAt(Instant.parse("2025-01-02T00:00:00Z"));
		return c;
	}

	private static CredentialsResponseDto expectedResponse(Credential c) {
		return CredentialsHttpsMapper.toResponse(c);
	}

	@Nested
	@DisplayName("POST /credentials (create)")
	class Create {

		@Test
		@DisplayName("should create credentials and return mapped response")
		void shouldCreate() {
			var dto = new CreateCredentialsDto(
					UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
					"test@test.com",
					"$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
					null);
			var created = sampleCredential();
			when(createCredentials.create(CredentialsHttpsMapper.toCreateCommand(dto))).thenReturn(created);

			CredentialsResponseDto result = controller.create(dto);

			ArgumentCaptor<CreateCredentialsCommand> captor = ArgumentCaptor.forClass(CreateCredentialsCommand.class);
			verify(createCredentials).create(captor.capture());
			assertThat(captor.getValue()).isEqualTo(CredentialsHttpsMapper.toCreateCommand(dto));
			assertThat(result).isEqualTo(expectedResponse(created));
		}
	}

	@Nested
	@DisplayName("GET /credentials/{publicId}")
	class GetByPublicId {

		@Test
		@DisplayName("should get user by publicId")
		void shouldGetByPublicId() {
			var found = sampleCredential();
			when(getCredentials.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).thenReturn(Optional.of(found));

			CredentialsResponseDto result = controller.get(PUBLIC_ID);

			verify(getCredentials).findByPublicId(argThat(q -> q.publicId().equals(PUBLIC_ID)));
			assertThat(result).isEqualTo(expectedResponse(found));
		}

		@Test
		@DisplayName("should throw 404 when not found")
		void shouldThrow404WhenNotFound() {
			when(getCredentials.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).thenReturn(Optional.empty());

			assertThatThrownBy(() -> controller.get(PUBLIC_ID))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
		}
	}

	@Nested
	@DisplayName("GET /credentials (list)")
	class ListEndpoints {

		@Test
		@DisplayName("should list by email")
		void shouldListByEmail() {
			var cred = sampleCredential();
			when(getCredentials.findByEmail(new FindByEmailQuery("user@example.com"))).thenReturn(Optional.of(cred));

			List<CredentialsResponseDto> result = controller.list("user@example.com", null);

			verify(getCredentials).findByEmail(argThat(q -> q.email().equals("user@example.com")));
			assertThat(result).containsExactly(expectedResponse(cred));
		}

		@Test
		@DisplayName("should list by userId")
		void shouldListByUserId() {
			UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
			var cred = sampleCredential();
			when(getCredentials.findByUserId(new FindByUserIdQuery(userId))).thenReturn(Optional.of(cred));

			List<CredentialsResponseDto> result = controller.list(null, userId);

			verify(getCredentials).findByUserId(argThat(q -> q.userId().equals(userId)));
			assertThat(result).containsExactly(expectedResponse(cred));
		}

		@Test
		@DisplayName("should return 400 when both email and userId are set")
		void shouldRejectBothEmailAndUserId() {
			assertThatThrownBy(() -> controller.list("a@b.com", UUID.randomUUID()))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
		}

		@Test
		@DisplayName("should return 400 when neither email nor userId is set")
		void shouldRequireOneFilter() {
			assertThatThrownBy(() -> controller.list(null, null))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
		}
	}

	@Nested
	@DisplayName("PATCH /credentials/{publicId}")
	class Update {

		@Test
		@DisplayName("should update credentials")
		void shouldUpdate() {
			var body = new UpdateCredentialsBodyDto(
					Optional.of("new@test.com"),
					Optional.empty(),
					Optional.empty(),
					Optional.empty());
			var updated = sampleCredential();
			updated.setEmail("new@test.com");

			UpdateCredentialsCommand expected =
					CredentialsHttpsMapper.toUpdateCommand(PUBLIC_ID, body);
			when(updateCredentials.update(expected)).thenReturn(updated);

			CredentialsResponseDto result = controller.update(PUBLIC_ID, body);

			verify(updateCredentials).update(expected);
			assertThat(result).isEqualTo(expectedResponse(updated));
		}
	}

	@Nested
	@DisplayName("DELETE /credentials/{publicId}")
	class Delete {

		@Test
		@DisplayName("should delete credentials")
		void shouldDelete() {
			ResponseEntity<ApiMessageResponse> result = controller.delete(PUBLIC_ID);

			verify(deleteCredentials).delete(new DeleteCredentialsCommand(PUBLIC_ID));
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(result.getBody()).isNotNull();
			assertThat(result.getBody().message()).isEqualTo("Credentials deleted successfully");
		}
	}
}
