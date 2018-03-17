package com.sceneit.chris.sceneit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.sceneit.chris.sceneit.main.gallery.GalleryImage;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    // TODO: Rename and change types of parameters
    private OnFragmentInteractionListener mListener;

    private MainModel mainModel = MainModel.getInstance();

    private CheckBox remainLoged, heatMap;

    private final int help_page_limit = 6;
    private ViewPager helpPager;
    private PagerAdapter pagerAdapter;

    private ImageButton nextHelp, previousHelp;

    private static final List<String> HELP_IMAGES = new ArrayList<>();

    static {
        HELP_IMAGES.add("home_help");
        HELP_IMAGES.add("profile_help");
        HELP_IMAGES.add("edit_image_help");
        HELP_IMAGES.add("gallery_help");
        HELP_IMAGES.add("gallery_comment_help");
        HELP_IMAGES.add("menu_help");
    }

    public HelpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HelpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        final SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(mainModel.getAuth().getCurrentUser().getUid(), Context.MODE_PRIVATE);

        remainLoged = (CheckBox) view.findViewById(R.id.set_login_cb);
        remainLoged.setChecked(sharedPreferences.getBoolean(MainModel.REMAIN_LOGGEDIN, true));
        remainLoged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = remainLoged.isChecked();
                sharedPreferences.edit().putBoolean(MainModel.REMAIN_LOGGEDIN, checked).apply();
            }
        });
        heatMap = (CheckBox) view.findViewById(R.id.set_heatmap_cb);
        heatMap.setChecked(sharedPreferences.getBoolean(MainModel.USE_HEATMAP, true));
        heatMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = heatMap.isChecked();
                sharedPreferences.edit().putBoolean(MainModel.USE_HEATMAP, checked).apply();
            }
        });

        helpPager = (ViewPager) view.findViewById(R.id.help_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        helpPager.setAdapter(pagerAdapter);

        previousHelp = (ImageButton) view.findViewById(R.id.set_prev);
        previousHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = helpPager.getCurrentItem();
                if(currentItem > 0) {
                    helpPager.setCurrentItem(currentItem - 1);
                }
            }
        });
        nextHelp = (ImageButton) view.findViewById(R.id.set_next);
        nextHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = helpPager.getCurrentItem();
                if(currentItem < help_page_limit - 1) {
                    helpPager.setCurrentItem(currentItem + 1);
                }
            }
        });
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
            return HelpImageFragment.newInstance(HELP_IMAGES.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return help_page_limit;
        }
    }
}
