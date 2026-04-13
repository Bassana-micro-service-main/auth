package com.auth.units.use_case.tokens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.application.use_case.tokens.CreateTokensUseCase;
import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.ports.in.tokens.CreateTokensInterfacePort.CreateTokensCommand;
import com.auth.domain.ports.out.TokensRepositoryPort;
import com.auth.lib.Utils;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateTokensUseCaseTest {

	private static final Instant FUTURE = Instant.now().plus(7, ChronoUnit.DAYS);

	@Mock
	private TokensRepositoryPort repository;

	private CreateTokensUseCase useCase;

	@BeforeEach
	void setUp() {
		useCase = new CreateTokensUseCase(repository);
	}

	@Test
	void shouldCreate() {
		var cmd = new CreateTokensCommand(TokenType.ACCESS, "val", FUTURE);
		when(repository.save(any(Token.class))).thenAnswer(inv -> inv.getArgument(0));

		Token result = useCase.create(cmd);

		ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);
		verify(repository).save(captor.capture());
		assertThat(captor.getValue().getPublicId()).matches(Utils.NANOID_REGEX.pattern());
		assertThat(captor.getValue().getType()).isEqualTo(TokenType.ACCESS);
		assertThat(result).isSameAs(captor.getValue());
	}

	@Test
	void validationBlocksSave() {
		assertThrows(BusinessError.class, () -> useCase.create(new CreateTokensCommand(null, "v", FUTURE)));
		verify(repository, never()).save(any());
	}
}
