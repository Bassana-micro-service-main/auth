package com.auth.infrastructure.database.hibernate.repository;

import com.auth.infrastructure.database.hibernate.entity.CredentialEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialEntityRepository extends JpaRepository<CredentialEntity, UUID> {

	Optional<CredentialEntity> findByPublicId(String publicId);

	Optional<CredentialEntity> findByEmail(String email);

	Optional<CredentialEntity> findByUserId(UUID userId);
}
