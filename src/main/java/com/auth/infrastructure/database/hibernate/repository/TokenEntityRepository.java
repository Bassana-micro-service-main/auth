package com.auth.infrastructure.database.hibernate.repository;

import com.auth.infrastructure.database.hibernate.entity.TokenEntity;
import com.auth.infrastructure.database.hibernate.enums.TokenType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenEntityRepository extends JpaRepository<TokenEntity, UUID> {

	Optional<TokenEntity> findByPublicId(String publicId);

	List<TokenEntity> findByType(TokenType type);

	Optional<TokenEntity> findByValue(String value);
}
