package com.auth.domain.ports.out;

import com.auth.domain.entities.MfaDevicesEntity;
import com.auth.domain.enums.MfaType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant (driven) : persistance des {@link MfaDevicesEntity}.
 */
public interface MfaDevicesRepositoryPort {

	String REPOSITORY_QUALIFIER = "mfaDevicesRepository";

	MfaDevicesEntity save(MfaDevicesEntity entity);

	Optional<MfaDevicesEntity> findById(UUID id);

	Optional<MfaDevicesEntity> findByPublicId(String publicId);

	List<MfaDevicesEntity> findByUserId(UUID userId);

	Optional<MfaDevicesEntity> findByUserIdAndType(UUID userId, MfaType type);

	void delete(String publicId);
}
