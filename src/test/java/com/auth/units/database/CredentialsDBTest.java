package com.auth.units.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.adapter.out.persistence.CredentialsRepositoryAdapter;
import com.auth.application.mappers.credentials.CredentialsDBMapper;
import com.auth.domain.entities.Credential;
import com.auth.infrastructure.database.hibernate.entity.CredentialEntity;
import com.auth.infrastructure.database.hibernate.repository.CredentialEntityRepository;
import java.time.Instant;
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

/**
 * Tests unitaires de l’adaptateur de persistance credentials (équivalent
 * {@code user.database.spec.ts} / Prisma : mock du dépôt bas niveau, mapper réel).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CredentialsRepositoryAdapter")
class CredentialsDBTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
	private static final Instant T_CREATED = Instant.parse("2025-01-15T10:00:00Z");
	private static final Instant T_UPDATED = Instant.parse("2025-01-16T10:00:00Z");

	@Mock
	private CredentialEntityRepository jpa;

	private CredentialsRepositoryAdapter repository;

	@BeforeEach
	void setUp() {
		repository = new CredentialsRepositoryAdapter(jpa);
	}

	private static Credential sampleDomainCredential() {
		return new Credential(
				null,
				PUBLIC_ID,
				USER_ID,
				"test@test.com",
				"$2a$10$hashed",
				null,
				Instant.parse("2025-01-01T00:00:00Z"),
				true,
				null,
				null);
	}

	private static CredentialEntity samplePersistenceEntity() {
		var e = new CredentialEntity();
		e.setId(UUID.fromString("00000000-0000-4000-8000-000000000001"));
		e.setPublicId(PUBLIC_ID);
		e.setUserId(USER_ID);
		e.setEmail("test@test.com");
		e.setHashedPassword("$2a$10$hashed");
		e.setPasswordSalt(null);
		e.setPasswordLastChangedAt(Instant.parse("2025-01-01T00:00:00Z"));
		e.setActive(true);
		e.setCreatedAt(T_CREATED);
		e.setUpdatedAt(T_UPDATED);
		return e;
	}

	@Nested
	@DisplayName("save")
	class Save {

		@Test
		@DisplayName("should persist new credential when no row exists for publicId")
		void shouldSaveNew() {
			var entity = sampleDomainCredential();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
			when(jpa.save(any(CredentialEntity.class)))
					.thenAnswer(invocation -> {
						CredentialEntity p = invocation.getArgument(0);
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

			Credential result = repository.save(entity);

			verify(jpa).findByPublicId(PUBLIC_ID);
			ArgumentCaptor<CredentialEntity> captor = ArgumentCaptor.forClass(CredentialEntity.class);
			verify(jpa).save(captor.capture());
			assertThat(captor.getValue().getPublicId()).isEqualTo(PUBLIC_ID);
			assertThat(captor.getValue().getEmail()).isEqualTo("test@test.com");
			assertThat(result).isEqualTo(CredentialsDBMapper.toDomain(captor.getValue()));
		}

		@Test
		@DisplayName("should merge id and createdAt from existing row when publicId already exists")
		void shouldMergeExistingOnSave() {
			CredentialEntity existing = samplePersistenceEntity();
			var domain = sampleDomainCredential();
			domain.setEmail("updated@test.com");

			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(existing));
			when(jpa.save(any(CredentialEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

			repository.save(domain);

			ArgumentCaptor<CredentialEntity> captor = ArgumentCaptor.forClass(CredentialEntity.class);
			verify(jpa).save(captor.capture());
			assertThat(captor.getValue().getId()).isEqualTo(existing.getId());
			assertThat(captor.getValue().getCreatedAt()).isEqualTo(existing.getCreatedAt());
			assertThat(captor.getValue().getEmail()).isEqualTo("updated@test.com");
		}
	}

	@Nested
	@DisplayName("findByPublicId")
	class FindByPublicId {

		@Test
		@DisplayName("should return credential when publicId exists")
		void shouldFind() {
			CredentialEntity row = samplePersistenceEntity();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(row));

			Optional<Credential> result = repository.findByPublicId(PUBLIC_ID);

			verify(jpa).findByPublicId(PUBLIC_ID);
			assertThat(result).contains(CredentialsDBMapper.toDomain(row));
		}

		@Test
		@DisplayName("should return empty when not found")
		void shouldReturnEmpty() {
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

			Optional<Credential> result = repository.findByPublicId(PUBLIC_ID);

			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("findByEmail")
	class FindByEmail {

		@Test
		@DisplayName("should return credential when email exists")
		void shouldFind() {
			CredentialEntity row = samplePersistenceEntity();
			when(jpa.findByEmail("test@test.com")).thenReturn(Optional.of(row));

			Optional<Credential> result = repository.findByEmail("test@test.com");

			verify(jpa).findByEmail("test@test.com");
			assertThat(result).contains(CredentialsDBMapper.toDomain(row));
		}

		@Test
		@DisplayName("should return empty when not found")
		void shouldReturnEmpty() {
			when(jpa.findByEmail("missing@test.com")).thenReturn(Optional.empty());

			assertThat(repository.findByEmail("missing@test.com")).isEmpty();
		}
	}

	@Nested
	@DisplayName("delete")
	class Delete {

		@Test
		@DisplayName("should delete by publicId when row exists")
		void shouldDelete() {
			CredentialEntity row = samplePersistenceEntity();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(row));

			repository.delete(PUBLIC_ID);

			verify(jpa).findByPublicId(PUBLIC_ID);
			verify(jpa).delete(row);
		}

		@Test
		@DisplayName("should not call delete when no row matches publicId")
		void shouldNoOpWhenMissing() {
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

			repository.delete(PUBLIC_ID);

			verify(jpa).findByPublicId(PUBLIC_ID);
			verify(jpa, never()).delete(any(CredentialEntity.class));
		}
	}
}
