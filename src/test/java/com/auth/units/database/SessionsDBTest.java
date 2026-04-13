package com.auth.units.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.adapter.out.persistence.SessionsRepositoryAdapter;
import com.auth.application.mappers.sessions.SessionsDBMapper;
import com.auth.domain.entities.Session;
import com.auth.infrastructure.database.hibernate.entity.SessionEntity;
import com.auth.infrastructure.database.hibernate.repository.SessionEntityRepository;
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
@DisplayName("SessionsRepositoryAdapter")
class SessionsDBTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
	private static final Instant T_CREATED = Instant.parse("2025-01-15T10:00:00Z");
	private static final Instant T_UPDATED = Instant.parse("2025-01-16T10:00:00Z");
	private static final Instant FUTURE = Instant.now().plus(30, ChronoUnit.DAYS);

	@Mock
	private SessionEntityRepository jpa;

	private SessionsRepositoryAdapter repository;

	@BeforeEach
	void setUp() {
		repository = new SessionsRepositoryAdapter(jpa);
	}

	private static Session sampleDomainSession() {
		return new Session(
				null,
				PUBLIC_ID,
				USER_ID,
				"10.0.0.1",
				"Mozilla/5.0",
				"Chrome",
				"refresh-opaque",
				FUTURE,
				false,
				null,
				null);
	}

	private static SessionEntity samplePersistenceSession() {
		var e = new SessionEntity();
		e.setId(UUID.fromString("00000000-0000-4000-8000-000000000001"));
		e.setPublicId(PUBLIC_ID);
		e.setUserId(USER_ID);
		e.setIpAddress("10.0.0.1");
		e.setUserAgent("Mozilla/5.0");
		e.setDeviceName("Chrome");
		e.setRefreshToken("refresh-opaque");
		e.setExpiresAt(FUTURE);
		e.setRevoked(false);
		e.setCreatedAt(T_CREATED);
		e.setUpdatedAt(T_UPDATED);
		return e;
	}

	@Nested
	@DisplayName("save")
	class Save {

		@Test
		void shouldSaveNew() {
			var entity = sampleDomainSession();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
			when(jpa.save(any(SessionEntity.class)))
					.thenAnswer(invocation -> {
						SessionEntity p = invocation.getArgument(0);
						if (p.getId() == null) {
							p.setId(UUID.fromString("11111111-1111-4111-8111-111111111111"));
						}
						if (p.getCreatedAt() == null) {
							p.setCreatedAt(T_CREATED);
						}
						if (p.getUpdatedAt() == null) {
							p.setUpdatedAt(T_UPDATED);
						}
						return p;
					});

			Session result = repository.save(entity);

			verify(jpa).findByPublicId(PUBLIC_ID);
			ArgumentCaptor<SessionEntity> captor = ArgumentCaptor.forClass(SessionEntity.class);
			verify(jpa).save(captor.capture());
			assertThat(captor.getValue().getPublicId()).isEqualTo(PUBLIC_ID);
			assertThat(result).isEqualTo(SessionsDBMapper.toDomain(captor.getValue()));
		}

		@Test
		void shouldMergeExistingOnSave() {
			SessionEntity existing = samplePersistenceSession();
			var domain = sampleDomainSession();
			domain.setDeviceName("Firefox");

			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(existing));
			when(jpa.save(any(SessionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

			repository.save(domain);

			ArgumentCaptor<SessionEntity> captor = ArgumentCaptor.forClass(SessionEntity.class);
			verify(jpa).save(captor.capture());
			assertThat(captor.getValue().getId()).isEqualTo(existing.getId());
			assertThat(captor.getValue().getCreatedAt()).isEqualTo(existing.getCreatedAt());
			assertThat(captor.getValue().getDeviceName()).isEqualTo("Firefox");
		}
	}

	@Nested
	@DisplayName("findByPublicId")
	class FindByPublicId {

		@Test
		void shouldFind() {
			SessionEntity row = samplePersistenceSession();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(row));

			Optional<Session> result = repository.findByPublicId(PUBLIC_ID);

			verify(jpa).findByPublicId(PUBLIC_ID);
			assertThat(result).contains(SessionsDBMapper.toDomain(row));
		}

		@Test
		void shouldReturnEmpty() {
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

			assertThat(repository.findByPublicId(PUBLIC_ID)).isEmpty();
		}
	}

	@Nested
	@DisplayName("findByUserId")
	class FindByUserId {

		@Test
		void shouldReturnList() {
			SessionEntity row = samplePersistenceSession();
			when(jpa.findByUserId(USER_ID)).thenReturn(List.of(row));

			List<Session> result = repository.findByUserId(USER_ID);

			verify(jpa).findByUserId(USER_ID);
			assertThat(result).containsExactly(SessionsDBMapper.toDomain(row));
		}
	}

	@Nested
	@DisplayName("findByRefreshToken")
	class FindByRefreshToken {

		@Test
		void shouldFind() {
			SessionEntity row = samplePersistenceSession();
			when(jpa.findByRefreshToken("opaque-rt")).thenReturn(Optional.of(row));

			Optional<Session> result = repository.findByRefreshToken("opaque-rt");

			verify(jpa).findByRefreshToken("opaque-rt");
			assertThat(result).contains(SessionsDBMapper.toDomain(row));
		}

		@Test
		void shouldReturnEmpty() {
			when(jpa.findByRefreshToken("missing")).thenReturn(Optional.empty());

			assertThat(repository.findByRefreshToken("missing")).isEmpty();
		}
	}

	@Nested
	@DisplayName("delete")
	class Delete {

		@Test
		void shouldDelete() {
			SessionEntity row = samplePersistenceSession();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(row));

			repository.delete(PUBLIC_ID);

			verify(jpa).findByPublicId(PUBLIC_ID);
			verify(jpa).delete(row);
		}

		@Test
		void shouldNoOpWhenMissing() {
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

			repository.delete(PUBLIC_ID);

			verify(jpa).findByPublicId(PUBLIC_ID);
			verify(jpa, never()).delete(any(SessionEntity.class));
		}
	}
}
