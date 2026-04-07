package com.auth.domain.entities;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Session utilisateur (équivalent métier de {@code sessions_table}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {

	private UUID id;
	private String publicId;
	private UUID userId;
	private String ipAddress;
	private String userAgent;
	private String deviceName;
	private String refreshToken;
	private Instant expiresAt;
	private boolean revoked;
	private Instant createdAt;
	private Instant updatedAt;
}
