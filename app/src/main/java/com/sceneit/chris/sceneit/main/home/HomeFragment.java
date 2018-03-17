package com.sceneit.chris.sceneit.main.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.R;
import com.sceneit.chris.sceneit.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, HomeContract.IHomeView {

    private OnFragmentInteractionListener mListener;

    private MainModel mainModel = MainModel.getInstance();

    private HomePresenter presenter;

    private MapView mMap;
    private GoogleMap mGoogleMap;

    private ImageButton profileButton, cameraButton, galleryButton;

    private boolean initLocation = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Setup map
        mMap = (MapView) rootView.findViewById(R.id.home_map);
        this.initLocation = false;
        this.cameraButton = (ImageButton) rootView.findViewById(R.id.home_camera_btn);

        this.cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).takePicture();
            }
        });

        this.galleryButton = (ImageButton) rootView.findViewById(R.id.home_gallery_btn);
        this.galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).showGallery();
            }
        });

        this.profileButton = (ImageButton) rootView.findViewById(R.id.home_profile_btn);
        this.profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).showOwnProfile();
            }
        });


        if (mMap != null) {
            mMap.onCreate(null);
            mMap.onResume();
            mMap.getMapAsync(this);
        }

        // Get permissions for location
        List<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredPermissions.add(Manifest.permission.CAMERA);

        ((MainActivity) getActivity()).mayRequestMultiple(requiredPermissions);

        this.presenter = new HomePresenter(this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        Activity a = getActivity();
        if (a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        this.presenter = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(this.presenter.getLocManager() != null) {
            this.presenter.getLocManager().removeUpdates(this.presenter);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(getContext() != null && googleMap != null) {
            this.presenter.mapSetup(googleMap);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
