package com.auth.domain.services.policies;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;

/**
 * MFA obligatoire : si le compte est soumis à l'obligation MFA, au moins un périphérique MFA actif doit être enregistré.
 * Le drapeau « obligatoire » et la présence de MFA sont fournis par la couche application (compte, agrégats lus via ports).
 */
public final class MfaMandatoryPolicy {

	private MfaMandatoryPolicy() {
	}

	/**
	 * @param mfaMandatoryForAccount      règle produit / attribut compte
	 * @param hasAtLeastOneActiveMfaDevice au moins un {@link com.auth.domain.entities.MfaDevicesEntity} actif pour l'utilisateur
	 */
	public static void requireEnrolledIfMandatory(boolean mfaMandatoryForAccount, boolean hasAtLeastOneActiveMfaDevice) {
		if (mfaMandatoryForAccount && !hasAtLeastOneActiveMfaDevice) {
			throw new BusinessError(CodesError.MFA_MANDATORY_NOT_ENROLLED);
		}
	}
}
