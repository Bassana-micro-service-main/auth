package com.auth.domain.ports.in.mfa_devices;

/**
 * Port entrant (driving) : suppression d'un périphérique MFA.
 */
public interface DeleteMfaDevicesInterfacePort {

	record DeleteMfaDevicesCommand(String publicId) {
	}

	void delete(DeleteMfaDevicesCommand command);
}
