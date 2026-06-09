package com.auth.application.errors;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Erreur liée à la requête HTTP (contrôleurs, filtres, parsing) — distincte de {@link com.auth.domain.errors.BusinessError}.
 */
public class HttpRequestError extends RuntimeException {

	private final CodesHttpError code;
	private final Map<String, Object> metadata;

	public HttpRequestError(CodesHttpError code) {
		this(code, null, null);
	}

	public HttpRequestError(CodesHttpError code, Map<String, Object> metadata) {
		this(code, metadata, null);
	}

	public HttpRequestError(CodesHttpError code, Map<String, Object> metadata, Throwable cause) {
		super(Objects.requireNonNull(code, "code").name(), cause);
		this.code = code;
		this.metadata = metadata == null || metadata.isEmpty()
				? Collections.emptyMap()
				: Map.copyOf(metadata);
	}

	public CodesHttpError getCode() {
		return code;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}
}
