package com.auth.units.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.adapter.in.tokens.ApiMessageResponse;
import com.auth.adapter.in.tokens.TokensControllerAdapter;
import com.auth.application.dto.tokens.CreateTokensDto;
import com.auth.application.dto.tokens.TokensResponseDto;
import com.auth.application.dto.tokens.UpdateTokensBodyDto;
import com.auth.application.mappers.tokens.TokensHttpsMapper;
import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort.CreateTokensCommand;
import com.auth.domain.ports.in.tokens.DeleteTokensInterfacePort;
import com.auth.domain.ports.in.tokens.DeleteTokensInterfacePort.DeleteTokensCommand;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByTypeQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByValueQuery;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort.UpdateTokensCommand;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
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
@DisplayName("TokensControllerAdapter")
class TokensControllerTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final Instant FUTURE = Instant.now().plus(7, ChronoUnit.DAYS);

	@Mock
	private CreateTokensInterfacePort createTokens;

	@Mock
	private GetTokensInterfacePort getTokens;

	@Mock
	private UpdateTokensInterfacePort updateTokens;

	@Mock
	private DeleteTokensInterfacePort deleteTokens;

	private TokensControllerAdapter controller;

	@BeforeEach
	void setUp() {
		controller = new TokensControllerAdapter(createTokens, getTokens, updateTokens, deleteTokens);
	}

	private static Token sampleToken() {
		var t = new Token();
		t.setPublicId(PUBLIC_ID);
		t.setType(TokenType.ACCESS);
		t.setValue("opaque");
		t.setExpiresAt(FUTURE);
		t.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
		return t;
	}

	@Nested
	@DisplayName("POST /tokens")
	class Create {

		@Test
		void shouldCreate() {
			var dto = new CreateTokensDto(TokenType.REFRESH, "val", FUTURE);
			var created = sampleToken();
			when(createTokens.create(TokensHttpsMapper.toCreateCommand(dto))).thenReturn(created);

			TokensResponseDto result = controller.create(dto);

			ArgumentCaptor<CreateTokensCommand> captor = ArgumentCaptor.forClass(CreateTokensCommand.class);
			verify(createTokens).create(captor.capture());
			assertThat(captor.getValue()).isEqualTo(TokensHttpsMapper.toCreateCommand(dto));
			assertThat(result).isEqualTo(TokensHttpsMapper.toResponse(created));
		}
	}

	@Nested
	@DisplayName("GET /tokens/{publicId}")
	class Get {

		@Test
		void shouldGet() {
			var found = sampleToken();
			when(getTokens.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).thenReturn(Optional.of(found));

			TokensResponseDto result = controller.get(PUBLIC_ID);

			verify(getTokens).findByPublicId(argThat(q -> q.publicId().equals(PUBLIC_ID)));
			assertThat(result).isEqualTo(TokensHttpsMapper.toResponse(found));
		}

		@Test
		void should404() {
			when(getTokens.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).thenReturn(Optional.empty());

			assertThatThrownBy(() -> controller.get(PUBLIC_ID))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
		}
	}

	@Nested
	@DisplayName("GET /tokens (list)")
	class ListEndpoints {

		@Test
		void shouldListByType() {
			var tok = sampleToken();
			when(getTokens.findByType(new FindByTypeQuery(TokenType.ACCESS))).thenReturn(List.of(tok));

			List<TokensResponseDto> result = controller.list(TokenType.ACCESS, null);

			verify(getTokens).findByType(argThat(q -> q.type() == TokenType.ACCESS));
			assertThat(result).containsExactly(TokensHttpsMapper.toResponse(tok));
		}

		@Test
		void shouldListByValue() {
			var tok = sampleToken();
			when(getTokens.findByValue(new FindByValueQuery("lookup"))).thenReturn(Optional.of(tok));

			List<TokensResponseDto> result = controller.list(null, "lookup");

			verify(getTokens).findByValue(argThat(q -> q.value().equals("lookup")));
			assertThat(result).containsExactly(TokensHttpsMapper.toResponse(tok));
		}

		@Test
		void shouldRejectBothFilters() {
			assertThatThrownBy(() -> controller.list(TokenType.ACCESS, "v"))
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
	@DisplayName("PATCH /tokens/{publicId}")
	class Update {

		@Test
		void shouldUpdate() {
			var body = new UpdateTokensBodyDto(Optional.of("new-val"), Optional.empty());
			var updated = sampleToken();
			UpdateTokensCommand expected = TokensHttpsMapper.toUpdateCommand(PUBLIC_ID, body);
			when(updateTokens.update(expected)).thenReturn(updated);

			TokensResponseDto result = controller.update(PUBLIC_ID, body);

			verify(updateTokens).update(expected);
			assertThat(result).isEqualTo(TokensHttpsMapper.toResponse(updated));
		}
	}

	@Nested
	@DisplayName("DELETE /tokens/{publicId}")
	class Delete {

		@Test
		void shouldDelete() {
			ResponseEntity<ApiMessageResponse> result = controller.delete(PUBLIC_ID);

			verify(deleteTokens).delete(new DeleteTokensCommand(PUBLIC_ID));
			assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(result.getBody().message()).isEqualTo("Token deleted successfully");
		}
	}
}
