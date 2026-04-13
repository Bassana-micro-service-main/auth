package com.auth.adapter.out.user_profile;

import com.auth.config.UserProfileProperties;
import com.auth.domain.errors.BusinessError;
import com.auth.domain.errors.CodesError;
import com.auth.domain.ports.out.UserProfileClientPort;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Client HTTP vers le service <em>user_profile</em> (utilisateurs dans une autre base).
 */
@Component
@Qualifier(UserProfileClientPort.QUALIFIER)
public class UserProfileRestAdapter implements UserProfileClientPort {

	private final RestClient client;
	private final UserProfileProperties props;

	public UserProfileRestAdapter(
			@Qualifier("userProfileRestClient") RestClient userProfileRestClient, UserProfileProperties props) {
		this.client = userProfileRestClient;
		this.props = props;
	}

	@Override
	public UUID createUser(String email) {
		if (props.getBaseUrl() == null || props.getBaseUrl().isBlank()) {
			throw new BusinessError(CodesError.USER_PROFILE_UNAVAILABLE);
		}
		try {
			JsonNode node = client.post()
					.uri(props.getCreateUserPath())
					.contentType(MediaType.APPLICATION_JSON)
					.body(new CreateUserRequest(email))
					.retrieve()
					.body(JsonNode.class);
			return parseUserId(node);
		} catch (RestClientResponseException e) {
			if (e.getStatusCode().value() == 409) {
				throw new BusinessError(CodesError.AUTH_EMAIL_ALREADY_REGISTERED);
			}
			throw new BusinessError(CodesError.USER_PROFILE_UNAVAILABLE);
		}
	}

	@Override
	public void assertUserActive(UUID userId) {
		if (props.getBaseUrl() == null || props.getBaseUrl().isBlank()) {
			return;
		}
		try {
			client.get()
					.uri(props.getGetUserPath(), userId)
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientResponseException e) {
			if (e.getStatusCode().value() == 404) {
				throw new BusinessError(CodesError.USER_PROFILE_USER_NOT_FOUND);
			}
			throw new BusinessError(CodesError.USER_PROFILE_UNAVAILABLE);
		}
	}

	private static UUID parseUserId(JsonNode node) {
		if (node == null) {
			throw new BusinessError(CodesError.USER_PROFILE_UNAVAILABLE);
		}
		if (node.hasNonNull("id")) {
			return UUID.fromString(node.get("id").asText());
		}
		if (node.hasNonNull("userId")) {
			return UUID.fromString(node.get("userId").asText());
		}
		throw new BusinessError(CodesError.USER_PROFILE_UNAVAILABLE);
	}

	private record CreateUserRequest(String email) {
	}
}
