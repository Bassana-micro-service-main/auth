package com.auth.domain.ports.out;

import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant (driven) : persistance des {@link MfaDevice}.
 */
public interface MfaDevicesRepositoryPort {

	String REPOSITORY_QUALIFIER = "mfaDevicesRepository";

	MfaDevice save(MfaDevice entity);

	Optional<MfaDevice> findById(UUID id);

	Optional<MfaDevice> findByPublicId(String publicId);

	List<MfaDevice> findByUserId(UUID userId);

	Optional<MfaDevice> findByUserIdAndType(UUID userId, MfaType type);

	void delete(String publicId);
}
