package com.auth.adapter.in.auth;

import com.auth.application.dto.auth.AuthResponseDto;
import com.auth.application.dto.auth.LoginRequestDto;
import com.auth.application.dto.auth.LogoutRequestDto;
import com.auth.application.dto.auth.RefreshRequestDto;
import com.auth.application.dto.auth.RegisterRequestDto;
import com.auth.application.mappers.auth.AuthHttpsMapper;
import com.auth.domain.ports.in.auth.LoginInterfacePort;
import com.auth.domain.ports.in.auth.LoginInterfacePort.LoginCommand;
import com.auth.domain.ports.in.auth.LogoutInterfacePort;
import com.auth.domain.ports.in.auth.LogoutInterfacePort.LogoutCommand;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort;
import com.auth.domain.ports.in.auth.RefreshTokenInterfacePort.RefreshTokenCommand;
import com.auth.domain.ports.in.auth.RegisterInterfacePort;
import com.auth.domain.ports.in.auth.RegisterInterfacePort.RegisterCommand;
import com.auth.domain.ports.in.auth.VerifyAccessTokenInterfacePort;
import com.auth.domain.ports.in.auth.VerifyAccessTokenInterfacePort.VerifyAccessTokenCommand;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints d’authentification (login, register, refresh, logout).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerAdapter {

	private final LoginInterfacePort login;
	private final RegisterInterfacePort register;
	private final RefreshTokenInterfacePort refreshToken;
	private final LogoutInterfacePort logout;
	private final VerifyAccessTokenInterfacePort verifyAccessToken;

	@PostMapping("/login")
	public AuthResponseDto login(@RequestBody LoginRequestDto dto, HttpServletRequest request) {
		var command = new LoginCommand(
				dto.email(),
				dto.password(),
				request.getRemoteAddr(),
				request.getHeader("User-Agent"),
				dto.deviceName());
		return AuthHttpsMapper.toResponse(login.login(command));
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponseDto register(@RequestBody RegisterRequestDto dto, HttpServletRequest request) {
		var command = new RegisterCommand(
				dto.email(),
				dto.password(),
				request.getRemoteAddr(),
				request.getHeader("User-Agent"),
				dto.deviceName());
		return AuthHttpsMapper.toResponse(register.register(command));
	}

	@PostMapping("/refresh")
	public AuthResponseDto refresh(@RequestBody RefreshRequestDto dto) {
		return AuthHttpsMapper.toResponse(
				refreshToken.refresh(new RefreshTokenCommand(dto.refreshToken())));
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(@RequestBody LogoutRequestDto dto) {
		logout.logout(new LogoutCommand(dto.refreshToken()));
	}

	@GetMapping("/verify")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void verify(HttpServletRequest request) {
		verifyAccessToken.verify(new VerifyAccessTokenCommand(request.getHeader("Authorization")));
	}
}
