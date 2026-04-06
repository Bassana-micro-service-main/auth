package com.auth.infrastructure.database.hibernate.entity;

import com.auth.infrastructure.database.hibernate.enums.TokenType;
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
@Table(name = "tokens_table")
@Getter
@Setter
@NoArgsConstructor
public class TokenEntity {

	@Id
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "public_id", nullable = false, unique = true, length = 21)
	private String publicId;

	@Column(name = "type", nullable = false, length = 32)
	private TokenType type;

	@Column(name = "value", nullable = false, length = 512)
	private String value;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
	}
}
