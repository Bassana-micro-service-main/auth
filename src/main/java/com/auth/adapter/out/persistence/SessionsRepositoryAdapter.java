package com.auth.adapter.out.persistence;

import com.auth.application.mappers.sessions.SessionsDBMapper;
import com.auth.domain.entities.Session;
import com.auth.domain.ports.out.SessionsRepositoryPort;
import com.auth.infrastructure.database.hibernate.entity.SessionEntity;
import com.auth.infrastructure.database.hibernate.repository.SessionEntityRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * Adaptateur de persistance pour {@link SessionsRepositoryPort} (équivalent NestJS repository + ORM).
 * Délègue à {@link SessionEntityRepository} et mappe via {@link SessionsDBMapper}.
 */
@RequiredArgsConstructor
public class SessionsRepositoryAdapter implements SessionsRepositoryPort {

	private final SessionEntityRepository jpa;

	@Override
	public Session save(Session entity) {
		SessionEntity persistence = SessionsDBMapper.toPersistence(entity);
		if (entity.getPublicId() != null) {
			jpa.findByPublicId(entity.getPublicId()).ifPresent(existing -> {
				persistence.setId(existing.getId());
				if (persistence.getCreatedAt() == null) {
					persistence.setCreatedAt(existing.getCreatedAt());
				}
			});
		}
		return SessionsDBMapper.toDomain(jpa.save(persistence));
	}

	@Override
	public Optional<Session> findById(UUID id) {
		return jpa.findById(id).map(SessionsDBMapper::toDomain);
	}

	@Override
	public Optional<Session> findByPublicId(String publicId) {
		return jpa.findByPublicId(publicId).map(SessionsDBMapper::toDomain);
	}

	@Override
	public List<Session> findByUserId(UUID userId) {
		return jpa.findByUserId(userId).stream().map(SessionsDBMapper::toDomain).toList();
	}

	@Override
	public Optional<Session> findByRefreshToken(String refreshToken) {
		return jpa.findByRefreshToken(refreshToken).map(SessionsDBMapper::toDomain);
	}

	@Override
	public void delete(String publicId) {
		jpa.findByPublicId(publicId).ifPresent(jpa::delete);
	}
}
