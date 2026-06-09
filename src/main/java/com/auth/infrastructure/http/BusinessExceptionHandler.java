package com.auth.infrastructure.http;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.ErrorRegistry;
import com.auth.infrastructure.external_services.audit.AuditClient;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Mappe {@link BusinessError} vers des réponses HTTP (code depuis {@link ErrorRegistry}).
 */
@RestControllerAdvice
public class BusinessExceptionHandler {

	private final AuditClient auditClient;

	public BusinessExceptionHandler(AuditClient auditClient) {
		this.auditClient = auditClient;
	}

	@ExceptionHandler(BusinessError.class)
	public ResponseEntity<Map<String, Object>> handle(BusinessError error) {
		auditClient.sendSystemError(error.getCode().name(), error.getMessage(), error);
		int status = ErrorRegistry.suggestedHttpStatus(error.getCode());
		return ResponseEntity.status(status)
				.body(Map.of(
						"code", error.getCode().name(),
						"message", error.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleUnexpected(Exception error) {
		auditClient.sendSystemError("UNEXPECTED_ERROR", error.getMessage(), error);
		return ResponseEntity.status(500)
				.body(Map.of(
						"code", "UNEXPECTED_ERROR",
						"message", "Unexpected server error"));
	}
}
