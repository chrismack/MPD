package com.sceneit.chris.sceneit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelpImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelpImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String drawableName;

    private OnFragmentInteractionListener mListener;

    private MainModel mainModel = MainModel.getInstance();

    private CheckBox remainLoged, heatMap;

    private ImageView image;

    public HelpImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param drawable resource.
     * @return A new instance of fragment HelpImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HelpImageFragment newInstance(String drawable) {
        HelpImageFragment fragment = new HelpImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, drawable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            drawableName = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_image, container, false);

        this.image = view.findViewById(R.id.help_image_iv);
        System.out.println(drawableName);
        try {
            int resourceId = getResources().getIdentifier(drawableName, "drawable", getActivity().getPackageName());
            this.image.setImageDrawable(getResources().getDrawable(resourceId, null));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
}
