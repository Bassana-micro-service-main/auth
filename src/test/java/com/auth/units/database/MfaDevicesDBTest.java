package com.auth.units.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.adapter.out.persistence.MfaDevicesRepositoryAdapter;
import com.auth.application.mappers.mfa_devices.MfaDevicesDBMapper;
import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import com.auth.infrastructure.database.hibernate.entity.MfaDeviceEntity;
import com.auth.infrastructure.database.hibernate.repository.MfaDeviceEntityRepository;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("MfaDevicesRepositoryAdapter")
class MfaDevicesDBTest {

	private static final String PUBLIC_ID = "abcdefghij12345678901";
	private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
	private static final Instant T_CREATED = Instant.parse("2025-01-15T10:00:00Z");

	private static final com.auth.infrastructure.database.hibernate.enums.MfaType PERSISTENCE_TOTP =
			com.auth.infrastructure.database.hibernate.enums.MfaType.TOTP;

	@Mock
	private MfaDeviceEntityRepository jpa;

	private MfaDevicesRepositoryAdapter repository;

	@BeforeEach
	void setUp() {
		repository = new MfaDevicesRepositoryAdapter(jpa);
	}

	private static MfaDevice sampleDomainDevice() {
		return new MfaDevice(
				null,
				PUBLIC_ID,
				USER_ID,
				MfaType.TOTP,
				"secret",
				null,
				"Authenticator",
				true,
				null,
				null);
	}

	private static MfaDeviceEntity samplePersistenceDevice() {
		var e = new MfaDeviceEntity();
		e.setId(UUID.fromString("00000000-0000-4000-8000-000000000001"));
		e.setPublicId(PUBLIC_ID);
		e.setUserId(USER_ID);
		e.setType(PERSISTENCE_TOTP);
		e.setSecret("secret");
		e.setPhoneNumber(null);
		e.setDeviceName("Authenticator");
		e.setActive(true);
		e.setLastUsedAt(null);
		e.setCreatedAt(T_CREATED);
		return e;
	}

	@Nested
	@DisplayName("save")
	class Save {

		@Test
		void shouldSaveNew() {
			var entity = sampleDomainDevice();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());
			when(jpa.save(any(MfaDeviceEntity.class)))
					.thenAnswer(invocation -> {
						MfaDeviceEntity p = invocation.getArgument(0);
						if (p.getId() == null) {
							p.setId(UUID.fromString("11111111-1111-4111-8111-111111111111"));
						}
						if (p.getCreatedAt() == null) {
							p.setCreatedAt(T_CREATED);
						}
						return p;
					});

			MfaDevice result = repository.save(entity);

			verify(jpa).findByPublicId(PUBLIC_ID);
			ArgumentCaptor<MfaDeviceEntity> captor = ArgumentCaptor.forClass(MfaDeviceEntity.class);
			verify(jpa).save(captor.capture());
			assertThat(captor.getValue().getPublicId()).isEqualTo(PUBLIC_ID);
			assertThat(result).isEqualTo(MfaDevicesDBMapper.toDomain(captor.getValue()));
		}

		@Test
		void shouldMergeExistingOnSave() {
			MfaDeviceEntity existing = samplePersistenceDevice();
			var domain = sampleDomainDevice();
			domain.setDeviceName("New device");

			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(existing));
			when(jpa.save(any(MfaDeviceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

			repository.save(domain);

			ArgumentCaptor<MfaDeviceEntity> captor = ArgumentCaptor.forClass(MfaDeviceEntity.class);
			verify(jpa).save(captor.capture());
			assertThat(captor.getValue().getId()).isEqualTo(existing.getId());
			assertThat(captor.getValue().getCreatedAt()).isEqualTo(existing.getCreatedAt());
			assertThat(captor.getValue().getDeviceName()).isEqualTo("New device");
		}
	}

	@Nested
	@DisplayName("findByPublicId")
	class FindByPublicId {

		@Test
		void shouldFind() {
			MfaDeviceEntity row = samplePersistenceDevice();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(row));

			Optional<MfaDevice> result = repository.findByPublicId(PUBLIC_ID);

			assertThat(result).contains(MfaDevicesDBMapper.toDomain(row));
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
			MfaDeviceEntity row = samplePersistenceDevice();
			when(jpa.findByUserId(USER_ID)).thenReturn(List.of(row));

			List<MfaDevice> result = repository.findByUserId(USER_ID);

			verify(jpa).findByUserId(USER_ID);
			assertThat(result).containsExactly(MfaDevicesDBMapper.toDomain(row));
		}
	}

	@Nested
	@DisplayName("findByUserIdAndType")
	class FindByUserIdAndType {

		@Test
		void shouldFind() {
			MfaDeviceEntity row = samplePersistenceDevice();
			when(jpa.findByUserIdAndType(USER_ID, PERSISTENCE_TOTP)).thenReturn(Optional.of(row));

			Optional<MfaDevice> result = repository.findByUserIdAndType(USER_ID, MfaType.TOTP);

			verify(jpa).findByUserIdAndType(USER_ID, PERSISTENCE_TOTP);
			assertThat(result).contains(MfaDevicesDBMapper.toDomain(row));
		}

		@Test
		void shouldReturnEmpty() {
			when(jpa.findByUserIdAndType(USER_ID, PERSISTENCE_TOTP)).thenReturn(Optional.empty());

			assertThat(repository.findByUserIdAndType(USER_ID, MfaType.TOTP)).isEmpty();
		}
	}

	@Nested
	@DisplayName("delete")
	class Delete {

		@Test
		void shouldDelete() {
			MfaDeviceEntity row = samplePersistenceDevice();
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.of(row));

			repository.delete(PUBLIC_ID);

			verify(jpa).delete(row);
		}

		@Test
		void shouldNoOpWhenMissing() {
			when(jpa.findByPublicId(PUBLIC_ID)).thenReturn(Optional.empty());

			repository.delete(PUBLIC_ID);

			verify(jpa, never()).delete(any(MfaDeviceEntity.class));
		}
	}
}
