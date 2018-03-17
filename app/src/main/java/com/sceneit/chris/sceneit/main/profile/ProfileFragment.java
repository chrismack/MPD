package com.sceneit.chris.sceneit.main.profile;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ProxyFileDescriptorCallback;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.R;
import com.sceneit.chris.sceneit.main.MainActivity;
import com.sceneit.chris.sceneit.main.data.Users;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PROF_ID = "prfileID";

    // TODO: Rename and change types of parameters
    private String mProfileID;

    private OnFragmentInteractionListener mListener;

    private MainModel mainModel = MainModel.getInstance();
    private DocumentSnapshot userDoc;

    boolean ownProfile;

    private ConstraintLayout ownLayout, otherLayout;

    private EditText editName;
    private TextView nameTv;

    private ImageButton changeName;

    private TabLayout tabs;

    private ScrollView imagesSV, followingSV;
    private LinearLayout imagesLV, followingLV;

    private Button followButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param profileID Parameter 1.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String profileID) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROF_ID, profileID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProfileID = getArguments().getString(ARG_PROF_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        this.ownLayout = (ConstraintLayout) view.findViewById(R.id.profile_own);
        this.otherLayout = (ConstraintLayout) view.findViewById(R.id.profile_other);

        this.editName = (EditText) view.findViewById(R.id.profile_edit_name);
        this.nameTv = (TextView) view.findViewById(R.id.profile_username_tv);

        this.changeName = (ImageButton) view.findViewById(R.id.profile_change_name_btn);

        this.imagesSV = (ScrollView) view.findViewById(R.id.profile_images);
        this.followingSV = (ScrollView) view.findViewById(R.id.profile_following);

        this.imagesLV = (LinearLayout) view.findViewById(R.id.profile_images_container);
        this.followingLV = (LinearLayout) view.findViewById(R.id.profile_following_container);

        this.tabs = (TabLayout) view.findViewById(R.id.profile_tabs);
        this.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTab(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        this.followButton = (Button) view.findViewById(R.id.profile_follow_btn);
        this.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followUser();
            }
        });

        final String profileID = mProfileID != null ? mProfileID : mainModel.getCurrentUser().getUid();
        this.ownProfile = profileID.equals(mainModel.getCurrentUser().getUid());

        final Query userQuery = mainModel.getDataStore().collection(MainModel.USER_COLLECTION)
                .whereEqualTo("uid", profileID);

        userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.size() == 1) {
                    userDoc = documentSnapshots.getDocuments().get(0);
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (userDoc != null) {
                    String profileName = (String) userDoc.get("name");
                    updateFollowButton();

                    if (ownProfile) {
                        ownLayout.setVisibility(View.VISIBLE);
                        otherLayout.setVisibility(View.GONE);

                        editName.setText(profileName);
                        changeName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                submitChangeName();
                            }
                        });
                    } else {
                        ownLayout.setVisibility(View.GONE);
                        otherLayout.setVisibility(View.VISIBLE);

                        nameTv.setText(profileName);
                    }

                    List<String> following = (List<String>) userDoc.get("following");
                    if (following != null) {
                        for (String uid : following) {
                            final LinearLayout container = new LinearLayout(getActivity().getApplicationContext());
                            container.setOrientation(LinearLayout.HORIZONTAL);
                            container.setPadding(0, 20, 0, 0);

                            Query followingUserQuery = mainModel.getDataStore().collection(MainModel.USER_COLLECTION)
                                    .whereEqualTo("uid", uid);

                            followingUserQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    final List<DocumentSnapshot> users = documentSnapshots.getDocuments();
                                    if (users.size() == 1) {
                                        SpannableString content = new SpannableString(users.get(0).get("name").toString());
                                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

                                        TextView username = new TextView(getActivity().getApplicationContext());
                                        username.setText(content);
                                        username.setTextColor(getResources().getColor(R.color.colorSecondary, null));
                                        username.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
                                        username.setTextColor(getResources().getColor(R.color.colorAccent, null));
                                        username.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ((MainActivity)getActivity()).showProfile(users.get(0).get("uid").toString());
                                            }
                                        });

                                        container.addView(username);
                                        if (ownProfile) {
                                            //TODO: add follow button
                                        }
                                        followingLV.addView(container);
                                    }
                                }
                            });
                        }
                    }
                }


            }
        });

        final Query userImages = mainModel.getDataStore().collection(MainModel.IMAGE_COLLECTION)
                .whereEqualTo("uid", profileID);

        userImages.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot doc : documentSnapshots.getDocuments()) {

                    final ProgressBar progressBar = new ProgressBar(getActivity().getApplicationContext());
                    progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent, null), PorterDuff.Mode.MULTIPLY);
                    progressBar.animate();

                    final TextView imagePoints = new TextView(getActivity().getApplicationContext());
                    imagePoints.setText(
                            getResources().getString(R.string.image_points) +
                                    doc.get("points").toString()
                    );
                    imagePoints.setTextColor(getResources().getColor(R.color.colorSecondary, null));
                    imagePoints.setPaddingRelative(0, 0, 0, 20);

                    final ImageView image = new ImageView(getActivity().getApplicationContext());

                    String fileName = doc.get("fileName").toString();

                    StorageReference storeRef = mainModel.getStorageReference();
                    StorageReference imageRef = storeRef.child("images/").child(fileName);

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getActivity().getApplicationContext())
                                    .load(uri)
                                    .listener(new RequestListener<Uri, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(image);
                        }
                    });

                    imagesLV.addView(imagePoints);
                    imagesLV.addView(image);
                    imagesLV.addView(progressBar);
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

    private void submitChangeName() {
        String name = editName.getText().toString().trim();
        editName.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        if (!name.equals("")) {

            userDoc.getReference().update("name", name)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Username Updated",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
    }


    private void switchTab(TabLayout.Tab tab) {
        String tabTitle = tab.getText().toString().toLowerCase();

        switch (tabTitle) {
            case "images":
                imagesSV.setVisibility(View.VISIBLE);
                followingSV.setVisibility(View.GONE);
                break;
            case "following":
                imagesSV.setVisibility(View.GONE);
                followingSV.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateFollowButton() {
        List<String> followers = (List<String>) userDoc.get("followers");
        if(followers != null) {
            if(followers.contains(mainModel.getUserDoc().getUid())) {
                followButton.setText(getResources().getString(R.string.unfollow));
            } else {
                followButton.setText(getResources().getString(R.string.follow));
            }
        }
    }

    private void followUser() {
        final Users currentUser = mainModel.getUserDoc();
        if (currentUser != null) {
            final Users toFollow = userDoc.toObject(Users.class);

            if (currentUser.getFollowers() == null)
                currentUser.setFollowers(new ArrayList<String>());
            if (currentUser.getFollowing() == null)
                currentUser.setFollowing(new ArrayList<String>());

            if (toFollow.getFollowers() == null) toFollow.setFollowers(new ArrayList<String>());
            if (toFollow.getFollowing() == null) toFollow.setFollowing(new ArrayList<String>());

            // UnFollow
            if (currentUser.getFollowing().contains(toFollow.getUid())) {
                currentUser.getFollowing().remove(toFollow.getUid());
                if (toFollow.getFollowers().contains(currentUser.getUid())) {
                    toFollow.getFollowers().remove(currentUser.getUid());
                }
            } else { // Follow
                currentUser.getFollowing().add(toFollow.getUid());
                if (!toFollow.getFollowers().contains(currentUser.getUid()))
                    toFollow.getFollowers().add(currentUser.getUid());
            }



            Query currentUserQuery = mainModel.getDataStore().collection(MainModel.USER_COLLECTION)
                    .whereEqualTo("uid", currentUser.getUid());

            currentUserQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    List<DocumentSnapshot> currentUserList = documentSnapshots.getDocuments();
                    if (currentUserList.size() == 1) {
                        currentUserList.get(0).getReference().set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userDoc.getReference().set(toFollow).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mainModel.setUserDoc(currentUser);
                                        mainModel.getDataStore().collection(MainModel.USER_COLLECTION)
                                                .whereEqualTo("uid", toFollow.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                                List<DocumentSnapshot> list = documentSnapshots.getDocuments() ;
                                                if(list.size() == 1) {
                                                    userDoc = list.get(0);
                                                }

                                                updateFollowButton();
                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "You are now following " + toFollow.getName(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }

    }

}
