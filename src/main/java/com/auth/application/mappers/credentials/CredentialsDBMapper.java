package com.auth.application.mappers.credentials;

import com.auth.domain.entities.Credential;
import com.auth.infrastructure.database.hibernate.entity.CredentialEntity;

/**
 * Mapper entre l'entité de persistance Hibernate et l'entité de domaine credentials.
 */
public final class CredentialsDBMapper {

	private CredentialsDBMapper() {
	}

	public static Credential toDomain(CredentialEntity persistence) {
		if (persistence == null) {
			return null;
		}
		return new Credential(
				persistence.getId(),
				persistence.getPublicId(),
				persistence.getUserId(),
				persistence.getEmail(),
				persistence.getHashedPassword(),
				persistence.getPasswordSalt(),
				persistence.getPasswordLastChangedAt(),
				persistence.isActive(),
				persistence.getCreatedAt(),
				persistence.getUpdatedAt());
	}

	/**
	 * Construit l'objet de persistance à partir du domaine.
	 * Les champs techniques (id / dates) peuvent rester gérés par l'ORM selon le cas d'usage.
	 */
	public static CredentialEntity toPersistence(Credential domain) {
		if (domain == null) {
			return null;
		}
		CredentialEntity persistence = new CredentialEntity();
		persistence.setId(domain.getId());
		persistence.setPublicId(domain.getPublicId());
		persistence.setUserId(domain.getUserId());
		persistence.setEmail(domain.getEmail());
		persistence.setHashedPassword(domain.getHashedPassword());
		persistence.setPasswordSalt(domain.getPasswordSalt());
		persistence.setPasswordLastChangedAt(domain.getPasswordLastChangedAt());
		persistence.setActive(domain.isActive());
		persistence.setCreatedAt(domain.getCreatedAt());
		persistence.setUpdatedAt(domain.getUpdatedAt());
		return persistence;
	}
}
