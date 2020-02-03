package com.github.philippheuer.credentialmanager.storage;

import com.github.philippheuer.credentialmanager.api.IOAuth2StorageBackend;
import com.github.philippheuer.credentialmanager.api.IStorageBackend;
import com.github.philippheuer.credentialmanager.domain.Credential;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemporaryStorageBackend implements IOAuth2StorageBackend {

    /**
     * Holds the Credentials
     */
    protected List<Credential> credentialStorage = new ArrayList<>();

    /**
     * Load the Credentials
     *
     * @return List Credential
     */
    @Override
    public List<Credential> loadCredentials() {
        return this.credentialStorage;
    }

    /**
     * Save the Credentials
     *
     * @param credentials List Credential
     */
    @Override
    public void saveCredentials(List<Credential> credentials) {
        this.credentialStorage = credentials;
    }

}
