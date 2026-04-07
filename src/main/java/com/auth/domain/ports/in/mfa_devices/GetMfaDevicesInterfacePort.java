package com.auth.domain.ports.in.mfa_devices;

import com.auth.domain.entities.MfaDevice;
import com.auth.domain.enums.MfaType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port entrant (driving) : consultation d'entités {@link MfaDevice}.
 * Les critères passent uniquement par des requêtes ({@code Query}).
 */
public interface GetMfaDevicesInterfacePort {

	record FindByPublicIdQuery(String publicId) {
	}

	record FindByUserIdQuery(UUID userId) {
	}

	record FindByUserIdAndTypeQuery(UUID userId, MfaType type) {
	}

	Optional<MfaDevice> findByPublicId(FindByPublicIdQuery query);

	List<MfaDevice> findByUserId(FindByUserIdQuery query);

	Optional<MfaDevice> findByUserIdAndType(FindByUserIdAndTypeQuery query);
}
