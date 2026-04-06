package com.auth.domain.services.policies;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import java.util.Objects;
import java.util.UUID;

/**
 * Périmètre utilisateur : une action sur une ressource n'est permise que si l'acteur est le propriétaire
 * (sauf rôles d'administration modélisés ailleurs).
 */
public final class UserScopePolicy {

	private UserScopePolicy() {
	}

	/**
	 * Vérifie que l'utilisateur authentifié ({@code actorUserId}) est bien le propriétaire de la ressource ({@code resourceOwnerUserId}).
	 */
	public static void requireActorOwnsResource(UUID actorUserId, UUID resourceOwnerUserId) {
		Objects.requireNonNull(actorUserId, "actorUserId");
		Objects.requireNonNull(resourceOwnerUserId, "resourceOwnerUserId");
		if (!actorUserId.equals(resourceOwnerUserId)) {
			throw new BusinessError(CodesError.ACCESS_DENIED);
		}
	}
}
