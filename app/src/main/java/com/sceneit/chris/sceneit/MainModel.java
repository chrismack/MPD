package com.sceneit.chris.sceneit;

import android.Manifest;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sceneit.chris.sceneit.main.data.Comments;
import com.sceneit.chris.sceneit.main.data.Image;
import com.sceneit.chris.sceneit.main.data.Users;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private FirebaseAuth auth = null;
    private FirebaseUser currentUser = null;
    private Users userDoc;

    // Image store
    private StorageReference storageReference = null;

    // Database store
    private final FirebaseFirestore dataStore = FirebaseFirestore.getInstance();

    private LocationManager locationManager = null;

    // Last location update from the phone
    private LatLng lastLoc = null;

    private static final Map<String, Integer> requestCodes;

    public static final String IMAGE_COLLECTION = "IMAGES";
    public static final String USER_COLLECTION = "USERS";

    public static final String REMAIN_LOGGEDIN = "login";
    public static final String USE_HEATMAP = "heatmap";

    private final String FGM = FirebaseInstanceId.getInstance().getToken();


    private boolean loadingNewImages = false;
    private List<DocumentSnapshot> imagesInRange = null;

    static {
        requestCodes = new HashMap<>();
        requestCodes.put(Manifest.permission.READ_CONTACTS, 0);
        requestCodes.put(Manifest.permission.CAMERA, 1);
        requestCodes.put(Manifest.permission.ACCESS_FINE_LOCATION, 2);
    }

    private MainModel() {
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
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

    public int getRequestCodeFor(String requestType) {
        return requestCodes.get(requestType).intValue();
    }

    public FirebaseFirestore getDataStore() {
        return this.dataStore;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public LatLng getLastLoc() {
        return lastLoc;
    }

    public void setLastLoc(LatLng lastLoc) {
        this.lastLoc = lastLoc;
    }

    public List<DocumentSnapshot> getImagesInRange() {
        return imagesInRange;
    }

    public void setImagesInRange(List<DocumentSnapshot> imagesInRange) {
        this.imagesInRange = imagesInRange;
    }

    public boolean isLoadingNewImages() {
        return loadingNewImages;
    }

    public void setLoadingNewImages(boolean loadingNewImages) {
        this.loadingNewImages = loadingNewImages;
    }

    public Users getUserDoc() {
        return userDoc;
    }

    public void setUserDoc(Users userDoc) {
        this.userDoc = userDoc;
    }

    public String getFGM() {
        return FGM;
    }

    public void updateFGM() {
        getDataStore().collection(USER_COLLECTION).whereEqualTo("uid", currentUser.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> userList = documentSnapshots.getDocuments();
                if(userList.size() == 1) {
                    Users user = userList.get(0).toObject(Users.class);
                    user.setFGM(FirebaseInstanceId.getInstance().getToken());
                    userDoc = user;
                }
            }
        });

    }
}
