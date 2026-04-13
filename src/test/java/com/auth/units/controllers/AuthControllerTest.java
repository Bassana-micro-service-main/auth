package com.auth.units.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth.adapter.in.auth.AuthControllerAdapter;
import com.auth.application.dto.auth.AuthResponseDto;
import com.auth.application.dto.auth.LoginRequestDto;
import com.auth.application.dto.auth.LogoutRequestDto;
import com.auth.application.dto.auth.RefreshRequestDto;
import com.auth.application.dto.auth.RegisterRequestDto;
import com.auth.application.mappers.auth.AuthHttpsMapper;
import com.auth.domain.ports.in.auth.AuthSessionResult;
import com.auth.domain.ports.in.auth.LoginInterfacePort;
import com.auth.domain.ports.in.auth.LoginInterfacePort.LoginCommand;
import com.auth.domain.ports.in.auth.LogoutInterfacePort;
import com.auth.domain.ports.in.auth.LogoutInterfacePort.LogoutCommand;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort.RefreshTokenCommand;
import com.auth.domain.ports.in.auth.RegisterInterfacePort;
import com.auth.domain.ports.in.auth.RegisterInterfacePort.RegisterCommand;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthControllerAdapter")
class AuthControllerTest {

	private static final Instant T1 = Instant.parse("2026-06-01T12:00:00Z");
	private static final Instant T2 = Instant.parse("2026-07-01T12:00:00Z");

	@Mock
	private LoginInterfacePort login;

	@Mock
	private RegisterInterfacePort register;

	@Mock
	private RefreshTokenInterfacePort refreshToken;

	@Mock
	private LogoutInterfacePort logout;

	private AuthControllerAdapter controller;

	private HttpServletRequest request;

	@BeforeEach
	void setUp() {
		controller = new AuthControllerAdapter(login, register, refreshToken, logout);
		request = mock(HttpServletRequest.class);
		when(request.getRemoteAddr()).thenReturn("192.168.1.10");
		when(request.getHeader("User-Agent")).thenReturn("JUnit-Agent/1.0");
	}

	private static AuthSessionResult sampleSessionResult() {
		return new AuthSessionResult("access-jwt", "refresh-opaque", T1, T2, "abcdefghij12345678901");
	}

	@Nested
	@DisplayName("POST /auth/login")
	class Login {

		@Test
		@DisplayName("should login and return mapped response")
		void shouldLogin() {
			var dto = new LoginRequestDto("user@test.com", "plain-pass", "Pixel");
			var session = sampleSessionResult();
			when(login.login(any(LoginCommand.class))).thenReturn(session);

			AuthResponseDto result = controller.login(dto, request);

			var expectedCmd = new LoginCommand(
					dto.email(), dto.password(), "192.168.1.10", "JUnit-Agent/1.0", dto.deviceName());
			ArgumentCaptor<LoginCommand> captor = ArgumentCaptor.forClass(LoginCommand.class);
			verify(login).login(captor.capture());
			assertThat(captor.getValue()).isEqualTo(expectedCmd);
			assertThat(result).isEqualTo(AuthHttpsMapper.toResponse(session));
		}
	}

	@Nested
	@DisplayName("POST /auth/register")
	class Register {

		@Test
		@DisplayName("should register and return mapped response")
		void shouldRegister() {
			var dto = new RegisterRequestDto("new@test.com", "Aa123456!", "Chrome");
			var session = sampleSessionResult();
			when(register.register(any(RegisterCommand.class))).thenReturn(session);

			AuthResponseDto result = controller.register(dto, request);

			var expectedCmd = new RegisterCommand(
					dto.email(), dto.password(), "192.168.1.10", "JUnit-Agent/1.0", dto.deviceName());
			ArgumentCaptor<RegisterCommand> captor = ArgumentCaptor.forClass(RegisterCommand.class);
			verify(register).register(captor.capture());
			assertThat(captor.getValue()).isEqualTo(expectedCmd);
			assertThat(result).isEqualTo(AuthHttpsMapper.toResponse(session));
		}
	}

	@Nested
	@DisplayName("POST /auth/refresh")
	class Refresh {

		@Test
		@DisplayName("should refresh tokens")
		void shouldRefresh() {
			var dto = new RefreshRequestDto("opaque-refresh");
			var session = sampleSessionResult();
			when(refreshToken.refresh(new RefreshTokenCommand("opaque-refresh"))).thenReturn(session);

			AuthResponseDto result = controller.refresh(dto);

			verify(refreshToken).refresh(new RefreshTokenCommand("opaque-refresh"));
			assertThat(result).isEqualTo(AuthHttpsMapper.toResponse(session));
		}
	}

	@Nested
	@DisplayName("POST /auth/logout")
	class Logout {

		@Test
		@DisplayName("should logout with refresh token")
		void shouldLogout() {
			var dto = new LogoutRequestDto("rt-value");

			controller.logout(dto);

			verify(logout).logout(new LogoutCommand("rt-value"));
		}
	}
}
