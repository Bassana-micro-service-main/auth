package com.auth.domain.entities;

import com.auth.domain.enums.TokenType;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Jeton de sécurité (équivalent métier de {@code tokens_table}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {

	private UUID id;
	private String publicId;
	private TokenType type;
	private String value;
	private Instant expiresAt;
	private Instant createdAt;
}
