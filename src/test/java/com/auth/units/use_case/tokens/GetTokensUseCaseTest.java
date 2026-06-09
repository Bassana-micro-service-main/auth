package com.auth.units.use_case.tokens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.tokens.GetTokensUseCase;
import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByTypeQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByValueQuery;
import com.auth.domain.ports.out.TokensRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetTokensUseCaseTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";

	@Mock
	private TokensRepositoryPort repository;

	private GetTokensUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new GetTokensUseCase(repository);
	}

	@Test
	void findByPublicId() {
		var t = new Token();
		when(repository.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(t));
		assertThat(useCase.findByPublicId(new FindByPublicIdQuery(PUBLIC_ID))).contains(t);
	}

	@Test
	void findByType() {
		var t = new Token();
		when(repository.findByType(TokenType.REFRESH)).thenReturn(List.of(t));
		assertThat(useCase.findByType(new FindByTypeQuery(TokenType.REFRESH))).containsExactly(t);
	}

	@Test
	void findByValue() {
		var t = new Token();
		when(repository.findByValue("v")).thenReturn(Optional.of(t));
		assertThat(useCase.findByValue(new FindByValueQuery("v"))).contains(t);
	}

	@Test
	void invalidPublicId() {
		assertThrows(BusinessError.class, () -> useCase.findByPublicId(new FindByPublicIdQuery("bad")));
		verify(repository, never()).findByPublicId(any());
	}

	@Test
	void nullType() {
		BusinessError ex = assertThrows(BusinessError.class, () -> useCase.findByType(new FindByTypeQuery(null)));
		assertThat(ex.getCode()).isEqualTo(CodesError.TOKENS_TYPE_INVALID);
	}
}
