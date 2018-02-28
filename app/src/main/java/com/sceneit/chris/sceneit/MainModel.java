package com.sceneit.chris.sceneit;

import android.Manifest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

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

//    private StorageReference storageReference;
    private FirebaseAuth auth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseUser currentUser = null;


    private static final Map<String, Integer> requestCodes;

    static {
        requestCodes = new HashMap<>();
        requestCodes.put(Manifest.permission.READ_CONTACTS, 0);
        requestCodes.put(Manifest.permission.CAMERA, 1);
    }

    private MainModel() {
//        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

//    public StorageReference getStorageReference() {
//        return storageReference;
//    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public int getRequestCodeFor(String requestType) {
        return requestCodes.get(requestType).intValue();
    }

}
