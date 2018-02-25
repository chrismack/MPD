package com.sceneit.chris.sceneit;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Chris on 24/02/2018.
 */

public class MainModel {

    /*
     * Singleton instance
     */
    private static final MainModel instance = new MainModel();

    public static MainModel getInstance() {
        return instance;
    }

    private StorageReference storageReference;
    private FirebaseAuth auth;

    private FirebaseUser currentUser = null;

    private MainModel() {
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }
}
