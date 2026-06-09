package com.auth.domain.services.validators.mfa_devices;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdAndTypeQuery;
import com.auth.domain.ports.in.mfa_devices.GetMfaDevicesInterfacePort.FindByUserIdQuery;
import com.auth.domain.services.validators.InputValidators;

public final class GetMfaDevicesQueriesValidators {

	private GetMfaDevicesQueriesValidators() {
	}

	public static void validate(FindByPublicIdQuery query) {
		InputValidators.requireNanoid(query.publicId(), CodesError.MFA_DEVICES_PUBLIC_ID_INVALID);
	}

	public static void validate(FindByUserIdQuery query) {
		InputValidators.requireNonNullUuid(query.userId(), CodesError.MFA_DEVICES_USER_ID_INVALID);
	}

	public static void validate(FindByUserIdAndTypeQuery query) {
		InputValidators.requireNonNullUuid(query.userId(), CodesError.MFA_DEVICES_USER_ID_INVALID);
		if (query.type() == null) {
			throw new BusinessError(CodesError.MFA_DEVICES_TYPE_INVALID);
		}
	}
}
