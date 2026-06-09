package com.auth.domain.entities;

import com.auth.domain.enums.MfaType;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Périphérique MFA (équivalent métier de {@code mfa_devices_table}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MfaDevice {

	private UUID id;
	private String publicId;
	private UUID userId;
	private MfaType type;
	private String secret;
	private String phoneNumber;
	private String deviceName;
	private boolean active;
	private Instant lastUsedAt;
	private Instant createdAt;
}
