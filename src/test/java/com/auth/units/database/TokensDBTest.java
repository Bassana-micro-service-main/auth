package com.auth.units.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.adapter.out.persistence.TokensRepositoryAdapter;
import com.auth.application.mappers.tokens.TokensDBMapper;
import com.auth.domain.entities.Token;
import com.auth.domain.enums.TokenType;
import com.auth.infrastructure.database.hibernate.entity.TokenEntity;
import com.auth.infrastructure.database.hibernate.repository.TokenEntityRepository;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("TokensRepositoryAdapter")
class TokensDBTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final Instant T_CREATED = Instant.parse("2025-01-15T10:00:00Z");
	private static final Instant FUTURE = Instant.now().plus(7, ChronoUnit.DAYS);

	/** Type côté JPA (package infra), distinct du {@link TokenType} domaine dans les imports. */
	private static final com.auth.infrastructure.database.hibernate.enums.TokenType PERSISTENCE_ACCESS =
			com.auth.infrastructure.database.hibernate.enums.TokenType.ACCESS;

	@Mock
	private TokenEntityRepository jpa;

	private TokensRepositoryAdapter repository;

	@BeforeEach
	void setUp() {
		repository = new TokensRepositoryAdapter(jpa);
	}

	private static Token sampleDomainToken() {
		return new Token(null, PUBLIC_ID, TokenType.ACCESS, "opaque-value", FUTURE, null);
	}

	private static TokenEntity samplePersistenceToken() {
		var e = new TokenEntity();
		e.setId(UUID.fromString("00000000-0000-4000-8000-000000000001"));
		e.setPublicId(PUBLIC_ID);
		e.setType(PERSISTENCE_ACCESS);
		e.setValue("opaque-value");
		e.setExpiresAt(FUTURE);
		e.setCreatedAt(T_CREATED);
		return e;
	}

	@Nested
	@DisplayName("save")
	class Save {

		@Test
		void shouldSaveNew() {
			var entity = sampleDomainToken();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
			when(jpa.save(any(TokenEntity.class)))
					.thenAnswer(invocation -> {
						TokenEntity p = invocation.getArgument(0);
						if (p.getId() == null) {
							p.setId(UUID.fromString("11111111-1111-4111-8111-111111111111"));
						}
						if (p.getCreatedAt() == null) {
							p.setCreatedAt(T_CREATED);
						}
						return p;
					});

			Token result = repository.save(entity);

			verify(jpa).findByPublicId(PUBLIC_ID);
			ArgumentCaptor<TokenEntity> captor = ArgumentCaptor.forClass(TokenEntity.class);
			verify(jpa).save(captor.capture());
			assertThat(captor.getValue().getPublicId()).isEqualTo(PUBLIC_ID);
			assertThat(result).isEqualTo(TokensDBMapper.toDomain(captor.getValue()));
		}

		@Test
		void shouldMergeExistingOnSave() {
			TokenEntity existing = samplePersistenceToken();
			var domain = sampleDomainToken();
			domain.setValue("new-value");

			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(existing));
			when(jpa.save(any(TokenEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

			repository.save(domain);

			ArgumentCaptor<TokenEntity> captor = ArgumentCaptor.forClass(TokenEntity.class);
			verify(jpa).save(captor.capture());
			assertThat(captor.getValue().getId()).isEqualTo(existing.getId());
			assertThat(captor.getValue().getCreatedAt()).isEqualTo(existing.getCreatedAt());
			assertThat(captor.getValue().getValue()).isEqualTo("new-value");
		}
	}

	@Nested
	@DisplayName("findByPublicId")
	class FindByPublicId {

		@Test
		void shouldFind() {
			TokenEntity row = samplePersistenceToken();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(row));

			Optional<Token> result = repository.findByPublicId(PUBLIC_ID);

			assertThat(result).contains(TokensDBMapper.toDomain(row));
		}

		@Test
		void shouldReturnEmpty() {
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

			assertThat(repository.findByPublicId(PUBLIC_ID)).isEmpty();
		}
	}

	@Nested
	@DisplayName("findByType")
	class FindByType {

		@Test
		void shouldReturnList() {
			TokenEntity row = samplePersistenceToken();
			when(jpa.findByType(PERSISTENCE_ACCESS)).thenReturn(List.of(row));

			List<Token> result = repository.findByType(TokenType.ACCESS);

			verify(jpa).findByType(PERSISTENCE_ACCESS);
			assertThat(result).containsExactly(TokensDBMapper.toDomain(row));
		}
	}

	@Nested
	@DisplayName("findByValue")
	class FindByValue {

		@Test
		void shouldFind() {
			TokenEntity row = samplePersistenceToken();
			when(jpa.findByValue("opaque-value")).thenReturn(Optional.of(row));

			Optional<Token> result = repository.findByValue("opaque-value");

			verify(jpa).findByValue("opaque-value");
			assertThat(result).contains(TokensDBMapper.toDomain(row));
		}

		@Test
		void shouldReturnEmpty() {
			when(jpa.findByValue("missing")).thenReturn(Optional.empty());

			assertThat(repository.findByValue("missing")).isEmpty();
		}
	}

	@Nested
	@DisplayName("delete")
	class Delete {

		@Test
		void shouldDelete() {
			TokenEntity row = samplePersistenceToken();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(row));

			repository.delete(PUBLIC_ID);

			verify(jpa).delete(row);
		}

		@Test
		void shouldNoOpWhenMissing() {
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

			repository.delete(PUBLIC_ID);

			verify(jpa, never()).delete(any(TokenEntity.class));
		}
	}
}
