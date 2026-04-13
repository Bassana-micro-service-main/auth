package com.auth.infrastructure.database.hibernate.repository;

import com.auth.infrastructure.database.hibernate.entity.MfaDeviceEntity;
import com.auth.infrastructure.database.hibernate.enums.MfaType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MfaDeviceEntityRepository extends JpaRepository<MfaDeviceEntity, UUID> {

	Optional<MfaDeviceEntity> findByPublicId(String publicId);

	List<MfaDeviceEntity> findByUserId(UUID userId);

	Optional<MfaDeviceEntity> findByUserIdAndType(UUID userId, MfaType type);
}
