package com.auth.domain.ports.out;

import java.util.UUID;

/**
 * Accès au service <em>user_profile</em> (autre base / autre service). Création d’utilisateur et contrôle d’existence.
 */
public interface UserProfileClientPort {

	String QUALIFIER = "userProfileClient";

	/**
	 * Crée un utilisateur côté user_profile et retourne son {@code id} interne (UUID).
	 */
	UUID createUser(String email);

	/**
	 * Vérifie que l’utilisateur existe toujours et est utilisable (ex. non supprimé).
	 */
	void assertUserActive(UUID userId);
}
