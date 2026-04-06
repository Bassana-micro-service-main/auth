package com.auth.domain.services.policies;

/**
 * Règle métier : après changement de mot de passe, toutes les sessions existantes de l'utilisateur
 * doivent être invalidées (révocation globale), pour limiter l'usage d'anciens refresh tokens.
 * <p>
 * La couche application appelle les ports sortants (sessions, éventuellement tokens de type refresh)
 * lorsque {@link #shouldRevokeAllSessionsOnPasswordChange()} est vrai.
 */
public final class PasswordChangeRevocationPolicy {

	private PasswordChangeRevocationPolicy() {
	}

	/**
	 * @return {@code true} : le cas d'usage « mise à jour du mot de passe » doit révoquer toutes les sessions de l'utilisateur.
	 */
	public static boolean shouldRevokeAllSessionsOnPasswordChange() {
		return true;
	}
}
