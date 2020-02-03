package com.github.philippheuer.credentialmanager.api;

import com.github.philippheuer.credentialmanager.domain.Credential;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Storage Backend Interface
 */
public interface IStorageBackend {

    /**
     * Load the Credentials
     *
     * @return List Credential
     */
    List<Credential> loadCredentials();

    /**
     * Save the Credentials
     *
     * @param credentials List Credential
     */
    void saveCredentials(List<Credential> credentials);

    /**
     * Gets a Credential by UserId
     *
     * @param userId User Id
     * @return Credential
     */
    default Optional<Credential> getCredentialByUserId(String userId) {
        return filter(null, userId).stream().findFirst();
    }
    
    /**
     * Find all credentials matching the given set of parameters, ignoring null values passed in.
     *
     * @param identityProvider The identity provider name to filter by, or null to accept any value.
     * @param userId The user ID to filter by, or null to accept any value.
     * @return A list of credentials matching the filter.
     */
    default List<Credential> filter(String identityProvider, String userId) {
        return loadCredentials().stream().filter(cred ->
                (identityProvider == null || identityProvider.equals(cred.getIdentityProvider()))
                        && (userId == null || userId.equals(cred.getUserId()))
        ).collect(Collectors.toList());
    }
}
