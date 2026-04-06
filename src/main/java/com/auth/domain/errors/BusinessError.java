package com.auth.domain.errors;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Erreur métier portant un {@link CodesError} et des métadonnées optionnelles pour les adaptateurs (logs, API).
 */
public class BusinessError extends RuntimeException {

	private final CodesError code;
	private final Map<String, Object> metadata;

	public BusinessError(CodesError code) {
		this(code, null, null);
	}

	public BusinessError(CodesError code, Map<String, Object> metadata) {
		this(code, metadata, null);
	}

	public BusinessError(CodesError code, Map<String, Object> metadata, Throwable cause) {
		super(Objects.requireNonNull(code, "code").name(), cause);
		this.code = code;
		this.metadata = metadata == null || metadata.isEmpty()
				? Collections.emptyMap()
				: Map.copyOf(metadata);
	}

	public CodesError getCode() {
		return code;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}
}
