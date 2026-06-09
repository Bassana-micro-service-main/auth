package com.auth.application.errors;

/**
 * Codes d'erreur de la couche application liés aux requêtes HTTP (contrat API, pas règle métier domaine).
 */
public enum CodesHttpError {

	// Corps
	INVALID_REQUEST_BODY,
	MALFORMED_JSON,
	EMPTY_REQUEST_BODY,
	REQUEST_BODY_TOO_LARGE,

	// En-têtes
	MISSING_REQUIRED_HEADER,
	INVALID_HEADER_VALUE,

	// Query string
	MISSING_QUERY_PARAMETER,
	INVALID_QUERY_PARAMETER,

	// Chemin / path
	INVALID_PATH_PARAMETER,

	// Sémantique HTTP
	UNSUPPORTED_MEDIA_TYPE,
	UNSUPPORTED_HTTP_METHOD,
}
