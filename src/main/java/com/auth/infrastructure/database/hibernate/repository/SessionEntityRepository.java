package com.auth.infrastructure.database.hibernate.repository;

import com.auth.infrastructure.database.hibernate.entity.SessionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionEntityRepository extends JpaRepository<SessionEntity, UUID> {

	Optional<SessionEntity> findByPublicId(String publicId);

	List<SessionEntity> findByUserId(UUID userId);

	Optional<SessionEntity> findByRefreshToken(String refreshToken);
}
