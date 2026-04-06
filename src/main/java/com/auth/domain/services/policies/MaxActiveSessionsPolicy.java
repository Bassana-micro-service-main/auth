package com.auth.domain.services.policies;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;

/**
 * Limite le nombre de sessions actives (non révoquées, non expirées) par utilisateur.
 * Le plafond est fourni par la couche application (configuration), pas figé ici.
 */
public final class MaxActiveSessionsPolicy {

	/** Valeur par défaut si aucune config n'est fournie (à surcharger en application). */
	public static final int DEFAULT_MAX_ACTIVE_SESSIONS_PER_USER = 5;

	private MaxActiveSessionsPolicy() {
	}

	/**
	 * @param currentActiveCount nombre de sessions déjà ouvertes pour l'utilisateur (hors révoquées / expirées selon ta définition métier)
	 * @param maxAllowed         plafond autorisé (&gt; 0)
	 */
	public static void ensureCanCreateSession(long currentActiveCount, int maxAllowed) {
		if (maxAllowed < 1) {
			throw new IllegalArgumentException("maxAllowed must be >= 1");
		}
		if (currentActiveCount >= maxAllowed) {
			throw new BusinessError(CodesError.SESSIONS_LIMIT_EXCEEDED);
		}
	}
}
