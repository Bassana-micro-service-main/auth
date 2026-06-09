package com.auth.domain.entities;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Identifiants de connexion (équivalent métier de {@code credentials_table}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Credential {

	private UUID id;
	private String publicId;
	private UUID userId;
	private String email;
	private String hashedPassword;
	private String passwordSalt;
	private Instant passwordLastChangedAt;
	private boolean active;
	private Instant createdAt;
	private Instant updatedAt;
}
