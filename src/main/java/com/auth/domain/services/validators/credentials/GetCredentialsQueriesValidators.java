package com.auth.domain.services.validators.credentials;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByEmailQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.credentials.GetCredentialsInterfacePort.FindByUserIdQuery;
import com.auth.domain.services.validators.InputValidators;
import com.auth.lib.Utils;

public final class GetCredentialsQueriesValidators {

	private GetCredentialsQueriesValidators() {
	}

	public static void validate(FindByPublicIdQuery query) {
		InputValidators.requireNanoid(query.publicId(), CodesError.CREDENTIALS_PUBLIC_ID_INVALID);
	}

	public static void validate(FindByEmailQuery query) {
		if (query.email() == null || !Utils.EMAIL_REGEX.matcher(query.email()).matches()) {
			throw new BusinessError(CodesError.CREDENTIALS_EMAIL_INVALID);
		}
	}

	public static void validate(FindByUserIdQuery query) {
		InputValidators.requireNonNullUuid(query.userId(), CodesError.CREDENTIALS_USER_ID_INVALID);
	}
}
