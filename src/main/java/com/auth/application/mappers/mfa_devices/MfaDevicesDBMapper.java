package com.auth.application.mappers.mfa_devices;

import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import com.auth.infrastructure.database.hibernate.entity.MfaDeviceEntity;

/**
 * Mapper entre l'entité de persistance Hibernate et l'entité de domaine MFA.
 */
public final class MfaDevicesDBMapper {

	private MfaDevicesDBMapper() {
	}

	public static MfaDevice toDomain(MfaDeviceEntity persistence) {
		if (persistence == null) {
			return null;
		}
		return new MfaDevice(
				persistence.getId(),
				persistence.getPublicId(),
				persistence.getUserId(),
				toDomainMfaType(persistence.getType()),
				persistence.getSecret(),
				persistence.getPhoneNumber(),
				persistence.getDeviceName(),
				persistence.isActive(),
				persistence.getLastUsedAt(),
				persistence.getCreatedAt());
	}

	public static MfaDeviceEntity toPersistence(MfaDevice domain) {
		if (domain == null) {
			return null;
		}
		MfaDeviceEntity persistence = new MfaDeviceEntity();
		persistence.setId(domain.getId());
		persistence.setPublicId(domain.getPublicId());
		persistence.setUserId(domain.getUserId());
		persistence.setType(toPersistenceMfaType(domain.getType()));
		persistence.setSecret(domain.getSecret());
		persistence.setPhoneNumber(domain.getPhoneNumber());
		persistence.setDeviceName(domain.getDeviceName());
		persistence.setActive(domain.isActive());
		persistence.setLastUsedAt(domain.getLastUsedAt());
		persistence.setCreatedAt(domain.getCreatedAt());
		return persistence;
	}

	private static MfaType toDomainMfaType(com.auth.infrastructure.database.hibernate.enums.MfaType persistence) {
		if (persistence == null) {
			return null;
		}
		return MfaType.valueOf(persistence.name());
	}

	private static com.auth.infrastructure.database.hibernate.enums.MfaType toPersistenceMfaType(MfaType domain) {
		if (domain == null) {
			return null;
		}
		return com.auth.infrastructure.database.hibernate.enums.MfaType.valueOf(domain.name());
	}
}
