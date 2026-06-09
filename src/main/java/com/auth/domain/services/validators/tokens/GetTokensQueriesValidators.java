package com.auth.domain.services.validators.tokens;

import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByPublicIdQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByTypeQuery;
import com.auth.domain.ports.in.tokens.GetTokensInterfacePort.FindByValueQuery;
import com.auth.domain.services.validators.InputValidators;

public final class GetTokensQueriesValidators {

	private GetTokensQueriesValidators() {
	}

	public static void validate(FindByPublicIdQuery query) {
		InputValidators.requireNanoid(query.publicId(), CodesError.TOKENS_PUBLIC_ID_INVALID);
	}

	public static void validate(FindByTypeQuery query) {
		if (query.type() == null) {
			throw new BusinessError(CodesError.TOKENS_TYPE_INVALID);
		}
	}

	public static void validate(FindByValueQuery query) {
		InputValidators.requireNonBlank(query.value(), CodesError.TOKENS_VALUE_INVALID);
	}
}
