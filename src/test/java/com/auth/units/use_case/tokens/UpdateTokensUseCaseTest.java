package com.auth.units.use_case.tokens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.tokens.UpdateTokensUseCase;
import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.UpdateTokensInterfacePort.UpdateTokensCommand;
import com.auth.domain.ports.out.TokensRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateTokensUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";

	@Mock
	private TokensRepositoryPort repository;

	private UpdateTokensUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new UpdateTokensUseCase(repository);
	}

	private Token existing() {
		var t = new Token();
		t.setPublicId(PUBLIC_ID);
		t.setType(TokenType.ACCESS);
		t.setValue("old");
		return t;
	}

	@Test
	void shouldUpdate() {
		var tok = existing();
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(tok));
		when(repository.save(any(Token.class))).thenAnswer(inv -> inv.getArgument(0));

		var cmd = new UpdateTokensCommand(PUBLIC_ID, Optional.of("new-val"), Optional.empty());
		assertThat(useCase.update(cmd).getValue()).isEqualTo("new-val");
	}

	@Test
	void notFound() {
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
		var cmd = new UpdateTokensCommand(PUBLIC_ID, Optional.of("x"), Optional.empty());
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.update(cmd));
		assertThat(ex.getCode()).isEqualTo(CodesError.TOKENS_NOT_FOUND);
	}
}
