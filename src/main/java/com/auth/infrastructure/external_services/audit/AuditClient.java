package com.auth.infrastructure.external_services.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditClient {

	private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
	private final ObjectMapper objectMapper;
	private final String baseUrl;

	public AuditClient(
			ObjectMapper objectMapper,
			@Value("${AUDIT_SERVICE_URL:http://audit-service:8300}") String auditServiceUrl) {
		this.objectMapper = objectMapper;
		this.baseUrl = auditServiceUrl.endsWith("/") ? auditServiceUrl.substring(0, auditServiceUrl.length() - 1) : auditServiceUrl;
	}

	public void sendAuditLog(String action, String entityName, String entityId, String status, String ipAddress, String userAgent) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("action", action);
		payload.put("entityName", entityName);
		payload.put("entityId", entityId);
		payload.put("status", status);
		payload.put("ipAddress", ipAddress == null ? "" : ipAddress);
		payload.put("userAgent", userAgent == null ? "" : userAgent);
		postJsonAsync("/audit-logs", payload);
	}

	public void sendSystemError(String errorCode, String errorMessage, Throwable throwable) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("service", "authentication");
		payload.put("errorCode", errorCode);
		payload.put("errorMessage", errorMessage);
		Map<String, Object> stack = new HashMap<>();
		stack.put("exception", throwable.getClass().getName());
		stack.put("message", throwable.getMessage());
		payload.put("stackTrace", stack);
		postJsonAsync("/system-errors", payload);
	}

	private void postJsonAsync(String path, Map<String, Object> payload) {
		CompletableFuture.runAsync(() -> {
			try {
				String body = objectMapper.writeValueAsString(payload);
				HttpRequest req = HttpRequest.newBuilder()
						.uri(URI.create(baseUrl + path))
						.timeout(Duration.ofSeconds(2))
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(body))
						.build();
				client.send(req, HttpResponse.BodyHandlers.discarding());
			} catch (Exception e) {
				log.warn("audit send failed: {}", e.getMessage());
			}
		});
	}
}
