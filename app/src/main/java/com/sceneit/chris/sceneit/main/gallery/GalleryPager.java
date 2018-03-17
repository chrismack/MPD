package com.sceneit.chris.sceneit.main.gallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.R;
import com.sceneit.chris.sceneit.main.data.Image;
import com.sceneit.chris.sceneit.main.data.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryPager.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryPager#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryPager extends Fragment {

    private OnFragmentInteractionListener mListener;

    private MainModel mainModel = MainModel.getInstance();

    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    private ProgressBar progressBar;

    private ImageButton next, prev;

    final List<DocumentSnapshot> imagesInRange = new ArrayList<>();
    public int imagesLength = 0;

    public GalleryPager() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GalleryPager.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryPager newInstance() {
        GalleryPager fragment = new GalleryPager();
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
        final View view = inflater.inflate(R.layout.gallery_pager, container, false);

        this.pager = view.findViewById(R.id.gallery_pager);
        this.progressBar = view.findViewById(R.id.gallery_progress);

        this.next = (ImageButton) view.findViewById(R.id.gal_pgr_next);
        this.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = pager.getCurrentItem();
                if(currentItem < imagesLength - 1) {
                    pager.setCurrentItem(currentItem + 1);
                }
            }
        });
        this.prev = (ImageButton) view.findViewById(R.id.gal_pgr_prev);
        this.prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = pager.getCurrentItem();
                if(currentItem > 0) {
                    pager.setCurrentItem(currentItem - 1);
                }
            }
        });
        showProgress(true);


        reloadImageList();


        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private ProgressBar getProgress() {
        return this.progressBar;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        /**
         * Shows the progress UI and hides the login form.
         */
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            getProgress().setVisibility(show ? View.VISIBLE : View.GONE);
            getProgress().animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    getProgress().setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            getProgress().setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

    }

    private Task reloadImageList() {
        /*
         * Load images for location
         */

        mainModel.setLoadingNewImages(true);

        final LatLng loc = mainModel.getLastLoc();

        LatLng topRight = SphericalUtil.computeOffset(loc, 5000, 0);
        topRight = SphericalUtil.computeOffset(topRight, 5000, 90);

        LatLng bottomLeft = SphericalUtil.computeOffset(loc, 5000, 180);
        bottomLeft = SphericalUtil.computeOffset(bottomLeft, 5000, 270);

        Query lat = mainModel.getDataStore().collection(MainModel.IMAGE_COLLECTION)
                .whereGreaterThan("lat", bottomLeft.latitude)
                .whereLessThan("lat", topRight.latitude)
                .orderBy("lat")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(100);

        final LatLng finalTopRight = topRight;
        final LatLng finalBottomLeft = bottomLeft;


        return lat.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        imagesInRange.clear();
                        List<DocumentSnapshot> docs = documentSnapshots.getDocuments();
                        for (DocumentSnapshot doc : docs) {
                            double lon = (double) doc.get("lon");
                            if (lon < finalTopRight.longitude && lon > finalBottomLeft.longitude) {
                                imagesInRange.add(doc);
                            }
                        }

                        List<DocumentSnapshot> sortedImages = sortImages(imagesInRange);
                        imagesInRange.clear();
                        imagesInRange.addAll(sortedImages);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mainModel.setLoadingNewImages(false);
                        mainModel.setImagesInRange(imagesInRange);
                        imagesLength = mainModel.getImagesInRange().size();

                        pager.setAdapter(pagerAdapter);
                        pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
                        pager.setAdapter(pagerAdapter);
                        pager.setOffscreenPageLimit(30);
                        pagerAdapter.notifyDataSetChanged();
//
//                        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                            @Override
//                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                            }
//
//                            @Override
//                            public void onPageSelected(int position) {
//                                System.out.println(position);
//                            }
//
//                            @Override
//                            public void onPageScrollStateChanged(int state) {
//                                System.out.println(state);
//                            }
//                        });


                        showProgress(false);
                    }
                });
    }

    private List<DocumentSnapshot> sortImages(List<DocumentSnapshot> images) {
        Map<String, List<DocumentSnapshot>> datesMap = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        // Group all images by date
        for(DocumentSnapshot doc : images) {
            Date date = (Date) doc.get("timestamp");
            String dateString = formatter.format(date);

            if(!datesMap.containsKey(dateString)) {
                datesMap.put(dateString, new ArrayList<DocumentSnapshot>());
            }

            datesMap.get(dateString).add(doc);
        }

        //Sort each days scores by points
        List<DocumentSnapshot> allImages = new ArrayList<>();

        for(Map.Entry<String, List<DocumentSnapshot>> days : datesMap.entrySet()) {
            List<DocumentSnapshot> sortedScores = new ArrayList<>();
            for(DocumentSnapshot doc : days.getValue()) {
                long points = (long) doc.get("points");
                if(sortedScores.size() > 0) {
                    boolean added = false;
                    for(int i = 0; i < sortedScores.size(); i++) {
                        if(points > (long) sortedScores.get(i).get("points")) {
                            sortedScores.add(i, doc);
                            added = true;
                            break;
                        }
                    }
                    if(!added) {
                        sortedScores.add(doc);
                    }
                } else {
                    sortedScores.add(doc);
                }
            }
            datesMap.put(days.getKey(), sortedScores);
        }

        List<String> sortedDates = new ArrayList<>();
        for(String date : datesMap.keySet()) {
            sortedDates.add(date);
        }

        Collections.sort(sortedDates, Collections.<String>reverseOrder());
        for(String s: sortedDates) {
            allImages.addAll(datesMap.get(s));
        }

        return allImages;
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return GalleryImage.newInstance("" + position, imagesInRange.get(position).getId());
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return imagesLength;
        }
    }
}
