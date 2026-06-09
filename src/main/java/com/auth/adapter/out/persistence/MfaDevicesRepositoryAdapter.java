package com.auth.adapter.out.persistence;

import com.auth.application.mappers.mfa_devices.MfaDevicesDBMapper;
import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import com.auth.domain.ports.out.MfaDevicesRepositoryPort;
import com.auth.infrastructure.database.hibernate.entity.MfaDeviceEntity;
import com.auth.infrastructure.database.hibernate.repository.MfaDeviceEntityRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * Adaptateur de persistance pour {@link MfaDevicesRepositoryPort} (équivalent NestJS repository + ORM).
 * Délègue à {@link MfaDeviceEntityRepository} et mappe via {@link MfaDevicesDBMapper}.
 */
@RequiredArgsConstructor
public class MfaDevicesRepositoryAdapter implements MfaDevicesRepositoryPort {

	private final MfaDeviceEntityRepository jpa;

	@Override
	public MfaDevice save(MfaDevice entity) {
		MfaDeviceEntity persistence = MfaDevicesDBMapper.toPersistence(entity);
		if (entity.getPublicId() != null) {
			jpa.findByPublicId(entity.getPublicId()).ifPresent(existing -> {
				persistence.setId(existing.getId());
				if (persistence.getCreatedAt() == null) {
					persistence.setCreatedAt(existing.getCreatedAt());
				}
			});
		}
		return MfaDevicesDBMapper.toDomain(jpa.save(persistence));
	}

	@Override
	public Optional<MfaDevice> findById(UUID id) {
		return jpa.findById(id).map(MfaDevicesDBMapper::toDomain);
	}

	@Override
	public Optional<MfaDevice> findByPublicId(String publicId) {
		return jpa.findByPublicId(publicId).map(MfaDevicesDBMapper::toDomain);
	}

	@Override
	public List<MfaDevice> findByUserId(UUID userId) {
		return jpa.findByUserId(userId).stream().map(MfaDevicesDBMapper::toDomain).toList();
	}

	@Override
	public Optional<MfaDevice> findByUserIdAndType(UUID userId, MfaType type) {
		return jpa.findByUserIdAndType(userId, toPersistenceMfaType(type)).map(MfaDevicesDBMapper::toDomain);
	}

	@Override
	public void delete(String publicId) {
		jpa.findByPublicId(publicId).ifPresent(jpa::delete);
	}

	private static com.auth.infrastructure.database.hibernate.enums.MfaType toPersistenceMfaType(MfaType domain) {
		if (domain == null) {
			return null;
		}
		return com.auth.infrastructure.database.hibernate.enums.MfaType.valueOf(domain.name());
	}
}
