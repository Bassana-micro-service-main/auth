package com.auth.infrastructure.database.hibernate.entity;

import com.auth.infrastructure.database.hibernate.enums.MfaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "mfa_devices_table")
@Getter
@Setter
@NoArgsConstructor
public class MfaDeviceEntity {

	@Id
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "public_id", nullable = false, unique = true, length = 21)
	private String publicId;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "type", nullable = false, length = 32)
	private MfaType type;

	@Column(name = "secret", length = 512)
	private String secret;

	@Column(name = "phone_number", length = 32)
	private String phoneNumber;

	@Column(name = "device_name", length = 255)
	private String deviceName;

	@Column(name = "is_active", nullable = false)
	private boolean active;

	@Column(name = "last_used_at")
	private Instant lastUsedAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
	}
}
