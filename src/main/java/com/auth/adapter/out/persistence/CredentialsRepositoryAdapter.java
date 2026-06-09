package com.auth.adapter.out.persistence;

import com.auth.application.mappers.credentials.CredentialsDBMapper;
import com.auth.domain.entities.Credential;
import com.auth.domain.ports.out.CredentialsRepositoryPort;
import com.auth.infrastructure.database.hibernate.entity.CredentialEntity;
import com.auth.infrastructure.database.hibernate.repository.CredentialEntityRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * Adaptateur de persistance pour {@link CredentialsRepositoryPort} (équivalent NestJS
 * {@code @Injectable()} + {@code UserRepositoryPort} + Prisma).
 * <p>
 * Délègue à {@link CredentialEntityRepository} (Spring Data / Hibernate) et mappe via {@link CredentialsDBMapper}.
 */
@RequiredArgsConstructor
public class CredentialsRepositoryAdapter implements CredentialsRepositoryPort {

	private final CredentialEntityRepository jpa;

	@Override
	public Credential save(Credential entity) {
		CredentialEntity persistence = CredentialsDBMapper.toPersistence(entity);
		if (entity.getPublicId() != null) {
			jpa.findByPublicId(entity.getPublicId()).ifPresent(existing -> {
				persistence.setId(existing.getId());
				if (persistence.getCreatedAt() == null) {
					persistence.setCreatedAt(existing.getCreatedAt());
				}
			});
		}
		return CredentialsDBMapper.toDomain(jpa.save(persistence));
	}

	@Override
	public Optional<Credential> findById(UUID id) {
		return jpa.findById(id).map(CredentialsDBMapper::toDomain);
	}

	@Override
	public Optional<Credential> findByPublicId(String publicId) {
		return jpa.findByPublicId(publicId).map(CredentialsDBMapper::toDomain);
	}

	@Override
	public Optional<Credential> findByEmail(String email) {
		return jpa.findByEmail(email).map(CredentialsDBMapper::toDomain);
	}

	@Override
	public Optional<Credential> findByUserId(UUID userId) {
		return jpa.findByUserId(userId).map(CredentialsDBMapper::toDomain);
	}

	@Override
	public void delete(String publicId) {
		jpa.findByPublicId(publicId).ifPresent(jpa::delete);
	}
}
