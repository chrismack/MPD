package com.sceneit.chris.sceneit.main.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.R;
import com.sceneit.chris.sceneit.main.MainActivity;
import com.sceneit.chris.sceneit.main.data.Comments;
import com.sceneit.chris.sceneit.main.data.Image;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryImage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryImage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryImage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "doc";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mDoc;

    private OnFragmentInteractionListener mListener;

    private MainModel mainModel = MainModel.getInstance();
    private DocumentReference docRef;
    private Image imagedata = null;
    private ImageView image;

    private Bitmap imageBitmap = null;

    private ImageButton upButton, downButton;

    private TextInputLayout commentLayout;
    private TextInputEditText comment;
    private ImageButton sendCommentButton;

    private TextView pointsTV, userTv;


    private LinearLayout commentsContainer;

    public String voteDir = "";

    private Context context;

    public GalleryImage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment GalleryImage.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryImage newInstance(String param1, String doc) {
        GalleryImage fragment = new GalleryImage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, doc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mDoc = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.gallery_image, container, false);

        image = (ImageView) view.findViewById(R.id.gallery_image_iv);
        upButton = (ImageButton) view.findViewById(R.id.gal_img_up);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upvoteImage();
            }
        });
        downButton = (ImageButton) view.findViewById(R.id.gal_img_down);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downvoteImage();
            }
        });

        comment = (TextInputEditText) view.findViewById(R.id.gal_img_comment);
        sendCommentButton = (ImageButton) view.findViewById(R.id.gallery_send_comment);
        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment();
            }
        });

        commentsContainer = (LinearLayout) view.findViewById(R.id.gallery_comment_container);
        commentLayout = (TextInputLayout) view.findViewById(R.id.image_comment_layout);

        pointsTV = (TextView) view.findViewById(R.id.gal_img_points);
        userTv = (TextView) view.findViewById(R.id.gal_img_usr);

        if (mainModel.getImagesInRange() != null && !mainModel.isLoadingNewImages()) {
            List<DocumentSnapshot> docs = mainModel.getImagesInRange();

            if (docs.size() > 0) {
                StorageReference ref = mainModel.getStorageReference();
                StorageReference imageRef = ref.child("images/" + docs.get(Integer.parseInt(mParam1)).get("fileName"));
                String filePath = (String) docs.get(0).get("fileName");
                System.out.println(filePath);


                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (getContext() != null) {
                            Glide.with(getActivity().getApplicationContext())
                                    .load(uri)
                                    .into(image);
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        System.out.println(task);
                    }
                });

            }
        }

        docRef = mainModel.getDataStore().collection(MainModel.IMAGE_COLLECTION).document(mDoc);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                imagedata = documentSnapshot.toObject(Image.class);

            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (imagedata != null) {
                    if (imagedata.getVotes().containsKey(mainModel.getCurrentUser().getUid())) {
                        updateVoteButtons(imagedata.getVotes().get(mainModel.getCurrentUser().getUid()));
                    }
                    updateComments();
                    updatePoints();

                    Query usernameQuery = mainModel.getDataStore().collection(MainModel.USER_COLLECTION)
                            .whereEqualTo("uid", imagedata.getUid());

                    usernameQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            List<DocumentSnapshot> userDoc = documentSnapshots.getDocuments();
                            if(userDoc.size() == 1) {
                                String username = userDoc.get(0).get("name").toString();
                                final String userUid = userDoc.get(0).get("uid").toString();

                                SpannableString content = new SpannableString(username);
                                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                                userTv.setText(content);

                                userTv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((MainActivity)getActivity()).showProfile(userUid);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (imageBitmap != null && image != null) {
            image.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void upvoteImage() {
        handleVote("up");
    }

    private void downvoteImage() {
        handleVote("down");
    }

    private void updateVoteButtons(String direction) {
        if (direction.equals("up")) {
            if (!voteDir.equals(direction)) {
                upButton.setColorFilter(Color.GREEN);
                voteDir = direction;
            } else {
                upButton.setColorFilter(context.getResources().getColor(R.color.colorSecondary, null));
                voteDir = "";
            }
            downButton.setColorFilter(context.getResources().getColor(R.color.colorSecondary, null));
        } else if (direction.equals("down")) {
            if (!voteDir.equals(direction)) {
                downButton.setColorFilter(Color.RED);
                voteDir = direction;
            } else {
                downButton.setColorFilter(context.getResources().getColor(R.color.colorSecondary, null));
                voteDir = "";
            }
            upButton.setColorFilter(context.getResources().getColor(R.color.colorSecondary, null));
        }
    }

    private void handleVote(String direction) {
        updateVoteButtons(direction);
        if (imagedata != null) {
            if (direction != "" && !voteDir.equals("")) {
                imagedata.getVotes().put(mainModel.getCurrentUser().getUid(), direction);
            } else {
                imagedata.getVotes().remove(mainModel.getCurrentUser().getUid());
            }
        }

        imagedata.setPoints(updatePoints());

        docRef.set(imagedata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    private void sendComment() {
        if (imagedata != null) {
            String lComment = comment.getText().toString().trim();

            List<Comments> commentsList = imagedata.getComments();
            if (!lComment.equals("")) {
                commentsList.add(new Comments(mainModel.getCurrentUser().getUid(), lComment, new Date()));
                imagedata.setComments(commentsList);

                docRef.set(imagedata).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("update");
                    }
                });
            }
            comment.setText("");

            InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            updateComments();
        }
    }

    private void updateComments() {
        commentsContainer.removeAllViews();
        List<Comments> commentsList = imagedata.getComments();

        for(int i = 0; i < commentsList.size(); i++) {
            Comments comment = commentsList.get(i);
            TextView commentView = new TextView(context);
            commentView.setText(comment.getValue());
            int colour = i % 2 == 0 ? R.color.colorSecondary : R.color.colorPrimary;
            commentView.setTextColor(context.getResources().getColor(colour, null));
            commentView.setPaddingRelative(20 , 0, 0, 10);
            commentsContainer.addView(commentView);
        }

    }

    private int updatePoints() {
        int points = 0;

        for(Map.Entry<String, String> vote : imagedata.getVotes().entrySet()) {
            if(vote.getValue().equals("up")) points++;
            else if(vote.getValue().equals("down")) points--;
        }

        pointsTV.setText(String.valueOf(points));

        return points;
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
