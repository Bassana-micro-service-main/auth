package com.auth.infrastructure.http;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcAuditConfiguration implements WebMvcConfigurer {

	private final AuditHttpInterceptor auditHttpInterceptor;

	public WebMvcAuditConfiguration(AuditHttpInterceptor auditHttpInterceptor) {
		this.auditHttpInterceptor = auditHttpInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(auditHttpInterceptor);
	}
}
