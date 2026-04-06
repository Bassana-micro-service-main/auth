package com.auth.domain.ports.in.sessions;

/**
 * Port entrant (driving) : suppression d'une session.
 */
public interface DeleteSessionsInterfacePort {

	record DeleteSessionsCommand(String publicId) {
	}

	void delete(DeleteSessionsCommand command);
}
