package com.sceneit.chris.sceneit.main.image;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.sceneit.chris.sceneit.R;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment implements ImageContract.IImageView {
    private OnFragmentInteractionListener mListener;

    private ImagePresenter presenter;

    private String imagePath;
    private int orientation;
    private double latitude;
    private double longitude;

    private ImageButton sendButton, closeButton, colourSelectButton;
    private View progressView;


    public ImageFragment() {
        // Required empty public constructor
        this.latitude = 0.0f;
        this.longitude = 0.0f;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageURL URL of image taken from camera.
     * @return A new instance of fragment ImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageFragment newInstance(String imageURL, int orientation, double latitude, double longitude) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();

        args.putString("imageURL", imageURL);
        args.putInt("orientation", orientation);
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imagePath = getArguments().getString("imageURL");
            orientation = getArguments().getInt("orientation");
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        this.presenter = new ImagePresenter(this);
        this.presenter.setLatitude(this.latitude);
        this.presenter.setLongitude(this.longitude);


        progressView = rootView.findViewById(R.id.image_progress);

        sendButton = (ImageButton) rootView.findViewById(R.id.img_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSend();
            }
        });

        closeButton = (ImageButton) rootView.findViewById(R.id.img_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClose();
            }
        });

        colourSelectButton = (ImageButton) rootView.findViewById(R.id.img_col_sel);
        colourSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog picker = ColorPickerDialog.createColorPickerDialog(getContext(), ColorPickerDialog.DARK_THEME);
                picker.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
                    @Override
                    public void onColorPicked(int color, String hexVal) {
                        System.out.println(color);
                        System.out.println(hexVal);
                        presenter.setColour(color);
                    }
                });
                if(!presenter.isSending()) {
                    picker.show();
                }
            }
        });

        this.presenter.setImageContainer((ConstraintLayout) rootView.findViewById(R.id.imageContainer));
        Bitmap bitmap = null;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        try {
            ContentResolver cr = getActivity().getContentResolver();
            bitmap = MediaStore.Images.Media.getBitmap(cr, Uri.parse(imagePath));


            // If Elif cannot get orientation rotate image based on width and height
            if (orientation == 0) {
                int bmWidth, bmHeight;
                bmWidth = bitmap.getWidth();
                bmHeight = bitmap.getHeight();

                if (bmWidth > bmHeight) {
                    orientation = 1;
                }
            }

            Bitmap rotated = this.presenter.rotate(bitmap, orientation);

            int width = rotated.getWidth();
            int height = rotated.getHeight();


            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            matrix.setRectToRect(new RectF(0, 0, width, height), new RectF(0, 0, metrics.widthPixels, metrics.heightPixels), Matrix.ScaleToFit.FILL);

//            bitmap = Bitmap.createScaledBitmap(bitmap, metrics.widthPixels, metrics.heightPixels, true);
            rotated = Bitmap.createBitmap(rotated, 0, 0, rotated.getWidth(), rotated.getHeight(), matrix, true);
            this.presenter.setBitmap(rotated);

            // Sets the bit map into a drawable view
            if (bitmap != null) {
                this.presenter.setAndReplaceBitmap(rotated);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


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
        if (a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        this.presenter = null;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSend() {
        if (!this.presenter.isSending()) {
            this.presenter.submitImage();
        }
    }

    @Override
    public void onClose() {
        if (!this.presenter.isSending()) {
            this.presenter.closeImage();
        } else {

        }
    }

    @Override
    public void onToggleDraw() {

    }

    @Override
    public View getProgress() {
        return this.progressView;
    }

    @Override
    public String getImagePath() {
        return this.imagePath;
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
