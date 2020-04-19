package com.github.philippheuer.credentialmanager;

import com.github.philippheuer.credentialmanager.domain.Credential;
import com.github.philippheuer.credentialmanager.domain.IdentityProvider;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Tag("unittest")
public class CredentialManagerTest {
    
    static String providerName = "testidp";
    
    /**
     * Test Builder
     */
    @Test
    @DisplayName("CredentialManagerBuilder")
    public void builder() {
        // build
        CredentialManager credentialManager = CredentialManagerBuilder.builder().build();

        // asserts
        assertNotNull(credentialManager.getStorageBackend(), "Storage Backend not registered!");
    }

    /**
     * Test - Add Credential
     */
    @Test
    @DisplayName("Save a credential")
    public void saveCredential() {
        // build
        CredentialManager credentialManager = CredentialManagerBuilder.builder().build();
        
        IdentityProvider provider = new OAuth2IdentityProvider(providerName, "oauth2", null, null, null, null, null) {
            @Override
            public Optional<OAuth2Credential> getAdditionalCredentialInformation(OAuth2Credential credential) {
                return Optional.of(credential);
            }
        };
        credentialManager.registerIdentityProvider(provider);
        
        // add credential
        Credential credential = new OAuth2Credential(providerName, "tokenHere");
        credentialManager.addCredential(providerName, credential);

        // asserts
        assertEquals(1, credentialManager.getCredentials().size(), "Credential wasn't added!");
    }

}

