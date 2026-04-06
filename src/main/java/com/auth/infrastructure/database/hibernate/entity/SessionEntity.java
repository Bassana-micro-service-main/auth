package com.auth.infrastructure.database.hibernate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "sessions_table")
@Getter
@Setter
@NoArgsConstructor
public class SessionEntity {

	@Id
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "public_id", nullable = false, unique = true, length = 21)
	private String publicId;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "ip_address", length = 45)
	private String ipAddress;

	@Column(name = "user_agent", length = 1024)
	private String userAgent;

	@Column(name = "device_name", length = 255)
	private String deviceName;

	@Column(name = "refresh_token", nullable = false, length = 512)
	private String refreshToken;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "is_revoked", nullable = false)
	private boolean revoked;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}
}
