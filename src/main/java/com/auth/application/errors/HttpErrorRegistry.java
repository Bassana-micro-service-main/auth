package com.auth.application.errors;

import java.util.EnumMap;
import java.util.Map;

/**
 * Associe chaque {@link CodesHttpError} à un code de statut HTTP (RFC 7231).
 * La couche application peut utiliser Spring {@link org.springframework.http.HttpStatus} côté adaptateur.
 */
public final class HttpErrorRegistry {

	private static final Map<CodesHttpError, Integer> HTTP_STATUS = build();

	private HttpErrorRegistry() {
	}

	private static Map<CodesHttpError, Integer> build() {
		Map<CodesHttpError, Integer> m = new EnumMap<>(CodesHttpError.class);
		put(m, 400,
				CodesHttpError.INVALID_REQUEST_BODY,
				CodesHttpError.MALFORMED_JSON,
				CodesHttpError.EMPTY_REQUEST_BODY,
				CodesHttpError.MISSING_REQUIRED_HEADER,
				CodesHttpError.INVALID_HEADER_VALUE,
				CodesHttpError.MISSING_QUERY_PARAMETER,
				CodesHttpError.INVALID_QUERY_PARAMETER,
				CodesHttpError.INVALID_PATH_PARAMETER);
		put(m, 413,
				CodesHttpError.REQUEST_BODY_TOO_LARGE);
		put(m, 415,
				CodesHttpError.UNSUPPORTED_MEDIA_TYPE);
		put(m, 405,
				CodesHttpError.UNSUPPORTED_HTTP_METHOD);
		return Map.copyOf(m);
	}

	private static void put(Map<CodesHttpError, Integer> m, int status, CodesHttpError... codes) {
		for (CodesHttpError c : codes) {
			m.put(c, status);
		}
	}

	/**
	 * Code HTTP suggéré pour l'erreur, ou 400 si non renseigné.
	 */
	public static int suggestedHttpStatus(CodesHttpError code) {
		return HTTP_STATUS.getOrDefault(code, 400);
	}
}
