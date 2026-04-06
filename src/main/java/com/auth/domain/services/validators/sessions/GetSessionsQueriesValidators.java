package com.auth.domain.services.validators.sessions;

import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByRefreshTokenQuery;
import com.auth.domain.ports.in.sessions.GetSessionsInterfacePort.FindByUserIdQuery;
import com.auth.domain.services.validators.InputValidators;

public final class GetSessionsQueriesValidators {

	private GetSessionsQueriesValidators() {
	}

	public static void validate(FindByPublicIdQuery query) {
		InputValidators.requireNanoid(query.publicId(), CodesError.SESSIONS_PUBLIC_ID_INVALID);
	}

	public static void validate(FindByUserIdQuery query) {
		InputValidators.requireNonNullUuid(query.userId(), CodesError.SESSIONS_USER_ID_INVALID);
	}

	public static void validate(FindByRefreshTokenQuery query) {
		InputValidators.requireNonBlank(query.refreshToken(), CodesError.SESSIONS_REFRESH_TOKEN_INVALID);
	}
}
