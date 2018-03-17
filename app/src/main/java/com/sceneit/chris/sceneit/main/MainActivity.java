package com.sceneit.chris.sceneit.main;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.maps.android.SphericalUtil;
import com.sceneit.chris.sceneit.HelpFragment;
import com.sceneit.chris.sceneit.HelpImageFragment;
import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.R;
import com.sceneit.chris.sceneit.main.data.Users;
import com.sceneit.chris.sceneit.main.data.utils.Utils;
import com.sceneit.chris.sceneit.main.gallery.GalleryPager;
import com.sceneit.chris.sceneit.main.gallery.GalleryImage;
import com.sceneit.chris.sceneit.main.home.HomeFragment;
import com.sceneit.chris.sceneit.main.image.ImageFragment;
import com.sceneit.chris.sceneit.main.profile.ProfileFragment;
import com.sceneit.chris.sceneit.user.login.LoginView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ImageFragment.OnFragmentInteractionListener,
        GalleryPager.OnFragmentInteractionListener,
        GalleryImage.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        HelpImageFragment.OnFragmentInteractionListener,
        LocationListener {

    // View Elements
    private FrameLayout fragmentContainer;
    private MainModel mainModel = MainModel.getInstance();

    private boolean showImageEdit = false;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainModel.setLocationManager((LocationManager) this.getSystemService(Context.LOCATION_SERVICE));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mainModel.getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            // Get the last know location of the device
            Location location = mainModel.getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng lastKnow = new LatLng(location.getLatitude(), location.getLongitude());
            mainModel.setLastLoc(lastKnow);
        }

        this.fragmentContainer = findViewById(R.id.fragment_container);
        if (this.fragmentContainer != null) {
            if (savedInstanceState != null) {
                return;
            }
            showFragment(HomeFragment.newInstance(), "HOME");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUserDoc();

        if (showImageEdit) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            int orientation = -1;

            try {
                ExifInterface ei = new ExifInterface(mCurrentPhotoPathAbsPath);
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }


            LatLng lastLoc = mainModel.getLastLoc();
            // Todo: handle null location
            ImageFragment fragment = ImageFragment.newInstance(
                    mCurrentPhotoPath.toString(),
                    orientation,
                    lastLoc.latitude,
                    lastLoc.longitude
            );

            transaction
                    .addToBackStack("IMAGE")
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            // showImageEdit set on activityResult, needs to be reset for next image
            showImageEdit = false;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager manager = getSupportFragmentManager();
            if(manager.getBackStackEntryCount() > 1) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public static Context getContext() {
        return context;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle the camera action
            showFragment(HomeFragment.newInstance(), "HOME");
        } else if (id == R.id.nav_profile) {
            showOwnProfile();
        } else if (id == R.id.nav_camera) {
            if (mayRequest(fragmentContainer, Manifest.permission.CAMERA)) {
                takePicture();
            }
        } else if (id == R.id.nav_gallery) {
            this.showGallery();
        } else if (id == R.id.nav_Help) {
            showFragment(HelpFragment.newInstance(), "HELP");
        } else if (id == R.id.nav_logout) {
            mainModel.getAuth().signOut();
            mainModel.setCurrentUser(null);

            Intent login = new Intent(this, LoginView.class);
            startActivity(login);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean mayRequest(View parentView, final String requestType) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(requestType) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(requestType)) {
            Snackbar.make(parentView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{requestType}, mainModel.getRequestCodeFor(requestType));
                        }
                    });
        } else {
            requestPermissions(new String[]{requestType}, mainModel.getRequestCodeFor(requestType));
        }
        return false;
    }

    public boolean mayRequestMultiple(List<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // Multi req codes will start at 100
        int reqCode = 10;

        List<String> requiredPermissions = new ArrayList<>();
        for (String perm : permissions) {
            if (checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(perm);
                reqCode = (reqCode * 10) + mainModel.getRequestCodeFor(perm);
            }
        }

        String[] permissionsArray = requiredPermissions.toArray(new String[requiredPermissions.size()]);
        if (permissionsArray.length > 0) {
            ActivityCompat.requestPermissions(this, permissionsArray, reqCode);
            return false;
        }

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println(uri);
    }

    Uri mCurrentPhotoPath;
    String mCurrentPhotoPathAbsPath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mainModel.getRequestCodeFor(Manifest.permission.CAMERA) &&
                resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null && !mCurrentPhotoPath.equals("")) {
                // On resume show the edit image screen
                showImageEdit = true;
            }
        }
    }


    // https://developer.android.com/training/camera/photobasics.html
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPathAbsPath = image.getAbsolutePath();
        mCurrentPhotoPath = Uri.fromFile(image);
        return image;
    }

    public void takePicture() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, mainModel.getRequestCodeFor(Manifest.permission.CAMERA));
                }
            }
        }
    }

    public void showGallery() {
        showFragment(GalleryPager.newInstance(), "GALLERY");
    }

    public void showProfile(String uid) {
        showFragment(ProfileFragment.newInstance(uid), "PROFILE");
    }

    public void showOwnProfile() {
        showProfile(mainModel.getCurrentUser().getUid());
    }

    public void showFragment(@NonNull Fragment fragment, String backTag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (backTag != null && backTag != "") {
            transaction.addToBackStack(backTag);
            transaction
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    private void setUserDoc() {
        String uid = mainModel.getCurrentUser().getUid();
        Query userQuery = mainModel.getDataStore().collection(MainModel.USER_COLLECTION)
                .whereEqualTo("uid", uid);

        userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> userList = documentSnapshots.getDocuments();
                if(userList.size() == 1) {
                    Users user = userList.get(0).toObject(Users.class);
                    mainModel.setUserDoc(user);
                }
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {

        LatLng lastLoc = new LatLng(location.getLatitude(), location.getLongitude());

        // If the users has moved more than 500 meters
        if (Utils.distanceBetween(mainModel.getLastLoc(), lastLoc) > 500) {
            // TODO: update the avaliable images
            System.out.println("DISTANCE");
        }

        mainModel.setLastLoc(lastLoc);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
