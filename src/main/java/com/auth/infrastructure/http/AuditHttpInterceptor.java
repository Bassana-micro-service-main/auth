package com.auth.infrastructure.http;

import com.auth.infrastructure.external_services.audit.AuditClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditHttpInterceptor implements HandlerInterceptor {

	private final AuditClient auditClient;

	public AuditHttpInterceptor(AuditClient auditClient) {
		this.auditClient = auditClient;
	}

	@Override
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
		String action = toAuditAction(request.getMethod(), request.getRequestURI());
		String status = response.getStatus() >= 200 && response.getStatus() < 400 ? "success" : "failed";
		auditClient.sendAuditLog(
				action,
				"auth_request",
				request.getRequestURI(),
				status,
				request.getRemoteAddr(),
				request.getHeader("User-Agent"));
	}

	private String toAuditAction(String method, String path) {
		String lowerPath = path == null ? "" : path.toLowerCase();
		if ("POST".equalsIgnoreCase(method) && lowerPath.endsWith("/login")) {
			return "login";
		}
		if ("POST".equalsIgnoreCase(method) && lowerPath.endsWith("/logout")) {
			return "logout";
		}
		if ("POST".equalsIgnoreCase(method)) {
			return "create";
		}
		if ("PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
			return "update";
		}
		if ("DELETE".equalsIgnoreCase(method)) {
			return "delete";
		}
		return "read";
	}
}
