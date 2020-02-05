package com.github.philippheuer.credentialmanager.api;

import com.github.philippheuer.credentialmanager.domain.Credential;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface IOAuth2StorageBackend extends IStorageBackend {
	
	//TODO: Document
	default List<OAuth2Credential> loadOAuth2Credentials() {
		return loadCredentials().stream()
				.filter(cred -> cred instanceof OAuth2Credential)
				.map(cred -> (OAuth2Credential) cred).collect(Collectors.toList());
	}
	
	//TODO: Document
	default void saveOAuth2Credentials(List<OAuth2Credential> credentials) {
		saveCredentials(new ArrayList<>(credentials));
	}
	
	/**
	 * Find all credentials matching the given set of parameters, ignoring null values passed in.
	 *
	 * @param identityProvider The name of the identity provider to filter by, or null to accept any value.
	 * @param userId The user ID to filter by, or null to accept any value.
	 * @param accessToken The access token to filter by, or null to accept any value.
	 * @param refreshToken The refresh token to filter by, or null to accept any value.
	 * @param userName The user name to filter by, or null to accept any value.
	 * @param scopes A list of scopes to require, or null to accept any value.
	 * @return A list of credentials matching the provided filter parameters.
	 */
	default List<OAuth2Credential> filter(
			String identityProvider,
			String userId,
			String accessToken,
			String refreshToken,
			String userName,
			List<String> scopes) {
		return loadOAuth2Credentials().stream().filter(cred ->
				(identityProvider == null || identityProvider.equals(cred.getIdentityProvider()))
						&& (userId == null || userId.equals(cred.getUserId()))
						&& (accessToken == null || accessToken.equals(cred.getAccessToken()))
						&& (refreshToken == null || refreshToken.equals(cred.getRefreshToken()))
						&& (userName == null || userName.equals(cred.getUserName()))
						&& (scopes == null || cred.getScopes().containsAll(scopes))
		).collect(Collectors.toList());
	}
	
	/**
	 * Find all credentials matching the given set of parameters, ignoring null values passed in.
	 *
	 * @param identityProvider The name of the identity provider to filter by, or null to accept any value.
	 * @param userId The user ID to filter by, or null to accept any value.
	 * @param userName The user name to filter by, or null to accept any value.
	 * @param scopes A list of scopes to require, or null to accept any value.
	 * @return A list of credentials matching the provided filter parameters.
	 */
	default List<OAuth2Credential> filter( //Overload because it's probably rare to filter by a token
	                                       String identityProvider,
	                                       String userId,
	                                       String userName,
	                                       List<String> scopes) {
		return filter(identityProvider, userId, null, null, userName, scopes);
	}

	@Override
	default List<Credential> filter(String identityProvider, String userId) {
		return new ArrayList<>(filter(identityProvider, userId, null, null, null, null));
	}
	
}
