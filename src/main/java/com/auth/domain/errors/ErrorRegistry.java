package com.auth.domain.errors;

import java.util.EnumMap;
import java.util.Map;

/**
 * Associe chaque {@link CodesError} à un code HTTP suggéré pour les adaptateurs (REST, etc.).
 * Le domaine ne dépend pas de frameworks web ; seuls des entiers sont stockés.
 */
public final class ErrorRegistry {

	private static final Map<CodesError, Integer> HTTP_STATUS = build();

	private ErrorRegistry() {
	}

	private static Map<CodesError, Integer> build() {
		Map<CodesError, Integer> m = new EnumMap<>(CodesError.class);
		putValidation(m, 400,
				CodesError.PUBLIC_ID_INVALID,
				CodesError.USER_ID_INVALID,
				CodesError.CREDENTIALS_EMAIL_INVALID,
				CodesError.CREDENTIALS_PASSWORD_INVALID,
				CodesError.CREDENTIALS_PUBLIC_ID_INVALID,
				CodesError.CREDENTIALS_USER_ID_INVALID,
				CodesError.SESSIONS_PUBLIC_ID_INVALID,
				CodesError.SESSIONS_USER_ID_INVALID,
				CodesError.SESSIONS_IP_ADDRESS_INVALID,
				CodesError.SESSIONS_USER_AGENT_INVALID,
				CodesError.SESSIONS_DEVICE_NAME_INVALID,
				CodesError.SESSIONS_EXPIRES_AT_INVALID,
				CodesError.MFA_DEVICES_PUBLIC_ID_INVALID,
				CodesError.MFA_DEVICES_USER_ID_INVALID,
				CodesError.MFA_DEVICES_TYPE_INVALID,
				CodesError.MFA_DEVICES_SECRET_INVALID,
				CodesError.MFA_DEVICES_PHONE_INVALID,
				CodesError.MFA_DEVICES_DEVICE_NAME_INVALID,
				CodesError.TOKENS_PUBLIC_ID_INVALID,
				CodesError.TOKENS_TYPE_INVALID,
				CodesError.TOKENS_VALUE_INVALID,
				CodesError.TOKENS_EXPIRES_AT_INVALID);
		put(m, 401,
				CodesError.SESSIONS_EXPIRED,
				CodesError.SESSIONS_REVOKED,
				CodesError.TOKENS_EXPIRED,
				CodesError.AUTH_INVALID_CREDENTIALS,
				CodesError.AUTH_REFRESH_TOKEN_INVALID,
				CodesError.SESSIONS_REFRESH_TOKEN_INVALID);
		put(m, 502, CodesError.USER_PROFILE_UNAVAILABLE);
		put(m, 403,
				CodesError.CREDENTIALS_INACTIVE,
				CodesError.MFA_DEVICES_INACTIVE,
				CodesError.ACCESS_DENIED,
				CodesError.MFA_MANDATORY_NOT_ENROLLED);
		put(m, 409, CodesError.AUTH_EMAIL_ALREADY_REGISTERED, CodesError.SESSIONS_LIMIT_EXCEEDED);
		put(m, 404,
				CodesError.USER_PROFILE_USER_NOT_FOUND,
				CodesError.CREDENTIALS_NOT_FOUND,
				CodesError.SESSIONS_NOT_FOUND,
				CodesError.MFA_DEVICES_NOT_FOUND,
				CodesError.TOKENS_NOT_FOUND);
		return Map.copyOf(m);
	}

	private static void putValidation(Map<CodesError, Integer> m, int status, CodesError... codes) {
		put(m, status, codes);
	}

	private static void put(Map<CodesError, Integer> m, int status, CodesError... codes) {
		for (CodesError c : codes) {
			m.put(c, status);
		}
	}

	/**
	 * Code HTTP suggéré pour l'erreur, ou 400 si non renseigné.
	 */
	public static int suggestedHttpStatus(CodesError code) {
		return HTTP_STATUS.getOrDefault(code, 400);
	}
}
