package com.auth.application.mappers.sessions;

import com.auth.domain.entities.Session;
import com.auth.infrastructure.database.hibernate.entity.SessionEntity;

/**
 * Mapper entre l'entité de persistance Hibernate et l'entité de domaine session.
 */
public final class SessionsDBMapper {

	private SessionsDBMapper() {
	}

	public static Session toDomain(SessionEntity persistence) {
		if (persistence == null) {
			return null;
		}
		return new Session(
				persistence.getId(),
				persistence.getPublicId(),
				persistence.getUserId(),
				persistence.getIpAddress(),
				persistence.getUserAgent(),
				persistence.getDeviceName(),
				persistence.getRefreshToken(),
				persistence.getExpiresAt(),
				persistence.isRevoked(),
				persistence.getCreatedAt(),
				persistence.getUpdatedAt());
	}

	public static SessionEntity toPersistence(Session domain) {
		if (domain == null) {
			return null;
		}
		SessionEntity persistence = new SessionEntity();
		persistence.setId(domain.getId());
		persistence.setPublicId(domain.getPublicId());
		persistence.setUserId(domain.getUserId());
		persistence.setIpAddress(domain.getIpAddress());
		persistence.setUserAgent(domain.getUserAgent());
		persistence.setDeviceName(domain.getDeviceName());
		persistence.setRefreshToken(domain.getRefreshToken());
		persistence.setExpiresAt(domain.getExpiresAt());
		persistence.setRevoked(domain.isRevoked());
		persistence.setCreatedAt(domain.getCreatedAt());
		persistence.setUpdatedAt(domain.getUpdatedAt());
		return persistence;
	}
}
