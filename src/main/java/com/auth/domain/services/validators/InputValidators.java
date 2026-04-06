package com.auth.domain.services.validators;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.lib.Utils;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Règles d'entrée communes pour les validateurs du domaine.
 */
public final class InputValidators {

	private InputValidators() {
	}

	public static void requireNonNullUuid(UUID id, CodesError code) {
		if (id == null) {
			throw new BusinessError(code);
		}
	}

	public static void requireNanoid(String publicId, CodesError code) {
		if (publicId == null || !Utils.NANOID_REGEX.matcher(publicId).matches()) {
			throw new BusinessError(code);
		}
	}

	public static void requireNonBlank(String value, CodesError code) {
		if (value == null || value.isBlank()) {
			throw new BusinessError(code);
		}
	}

	public static void requireOptionalNonBlankIfPresent(Optional<String> value, CodesError code) {
		if (value.isPresent() && value.get().isBlank()) {
			throw new BusinessError(code);
		}
	}

	/** Expiration strictement dans le futur (création / renouvellement). */
	public static void requireFutureInstant(Instant instant, CodesError code) {
		if (instant == null || !instant.isAfter(Instant.now())) {
			throw new BusinessError(code);
		}
	}

	public static void requireNonNullInstant(Instant instant, CodesError code) {
		if (instant == null) {
			throw new BusinessError(code);
		}
	}
}
