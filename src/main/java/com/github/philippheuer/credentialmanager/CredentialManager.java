package com.github.philippheuer.credentialmanager;

import com.github.philippheuer.credentialmanager.api.IStorageBackend;
import com.github.philippheuer.credentialmanager.domain.AuthenticationController;
import com.github.philippheuer.credentialmanager.domain.Credential;
import com.github.philippheuer.credentialmanager.domain.IdentityProvider;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The CredentialManager
 */
@Getter
@Slf4j
public class CredentialManager {

    /**
     * Storage Backend
     */
    private final IStorageBackend storageBackend;

    /**
     * Authentication Controller
     */
    private final AuthenticationController authenticationController;

    /**
     * Holds the registered identity providers
     */
    private final Map<String, IdentityProvider> identityProviders = new HashMap<>();

    /**
     * In-Memory Credential Storage
     */
    private List<Credential> credentials;

    /**
     * Creates a new CredentialManager
     *
     * @param storageBackend           The Storage Backend
     * @param authenticationController Authentication Controller
     */
    public CredentialManager(IStorageBackend storageBackend, AuthenticationController authenticationController) {
        this.storageBackend = storageBackend;
        this.authenticationController = authenticationController;
        authenticationController.setCredentialManager(this);

        // load credentials
        this.load();
    }

    /**
     * Registers a new Identity Provider
     *
     * @param identityProvider Identity Provider
     */
    public void registerIdentityProvider(IdentityProvider identityProvider) {
        log.debug("Trying to register IdentityProvider {} [Type: {}]", identityProvider.getProviderName(), identityProvider.getProviderType());
        if(this.identityProviders.putIfAbsent(identityProvider.getProviderName().toLowerCase(), identityProvider) != null) {
            throw new RuntimeException("Identity Provider " + identityProvider.getProviderName() + " was already registered!");
        }
        identityProvider.setCredentialManager(this);
        log.debug("Registered IdentityProvider {} [Type: {}]", identityProvider.getProviderName(), identityProvider.getProviderType());
        log.debug("A total of {} IdentityProviders have been registered!", this.identityProviders.size());
    }

    /**
     * Get Identity Provider by Name
     *
     * @param identityProviderName Identity Provider Name
     * @return IdentityProvider
     */
    public Optional<IdentityProvider> getIdentityProviderByName(String identityProviderName) {
        return Optional.ofNullable(this.identityProviders.get(identityProviderName.toLowerCase()));
    }

    /**
     * Get OAuth2 Identity Provider by Name
     *
     * @param identityProviderName Identity Provider Name
     * @return IdentityProvider
     */
    public Optional<OAuth2IdentityProvider> getOAuth2IdentityProviderByName(String identityProviderName) {
        return Optional.ofNullable((OAuth2IdentityProvider) this.identityProviders.get(identityProviderName.toLowerCase()));
    }

    /**
     * Adds a Credential
     *
     * @param providerName Provider Name
     * @param credential   Credential
     */
    public void addCredential(String providerName, Credential credential) {
        // OAuth2
        if (credential instanceof OAuth2Credential) {
            IdentityProvider idp = getIdentityProviderByName(providerName).orElseThrow(() -> new RuntimeException("No provider found named " + providerName));
            if(idp instanceof OAuth2IdentityProvider) {
                Optional<OAuth2Credential> enrichedCredential = ((OAuth2IdentityProvider) idp).getAdditionalCredentialInformation((OAuth2Credential) credential);
                if (enrichedCredential.isPresent()) {
                    credential = enrichedCredential.get();
                }
            } else {
                throw new RuntimeException("Credential is an OAuth2Credential, but provider named \"" + providerName + "\" is not an OAuth2IdentityProvider");
            }
        }

        this.credentials.add(credential);
    }

    /**
     * Gets a OAuth2Credential by UserId
     *
     * @param userId User Id
     * @return OAuth2Credential
     */
    public Optional<OAuth2Credential> getOAuth2CredentialByUserId(String userId) {
        for (Credential entry : this.credentials) {
            if (entry instanceof OAuth2Credential) {
                OAuth2Credential credential = (OAuth2Credential) entry;

                if (credential.getUserId().equalsIgnoreCase(userId)) {
                    return Optional.of(credential);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Loads the Credentials from the Storage Backend
     */
    public void load() {
        this.credentials = storageBackend.loadCredentials();
    }

    /**
     * Persist the Credentials into the Storage Backend
     */
    public void save() {
        this.storageBackend.saveCredentials(credentials);
    }

}
