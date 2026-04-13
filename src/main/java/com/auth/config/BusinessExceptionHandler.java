package com.auth.config;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.ErrorRegistry;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Mappe {@link BusinessError} vers des réponses HTTP (code depuis {@link ErrorRegistry}).
 */
@RestControllerAdvice
public class BusinessExceptionHandler {

	@ExceptionHandler(BusinessError.class)
	public ResponseEntity<Map<String, Object>> handle(BusinessError error) {
		int status = ErrorRegistry.suggestedHttpStatus(error.getCode());
		return ResponseEntity.status(status)
				.body(Map.of(
						"code", error.getCode().name(),
						"message", error.getMessage()));
	}
}
