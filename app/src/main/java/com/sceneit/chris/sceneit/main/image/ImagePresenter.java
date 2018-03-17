package com.sceneit.chris.sceneit.main.image;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sceneit.chris.sceneit.MainModel;
import com.sceneit.chris.sceneit.main.MainActivity;
import com.sceneit.chris.sceneit.main.data.Comments;
import com.sceneit.chris.sceneit.main.data.Image;
import com.sceneit.chris.sceneit.main.home.HomeFragment;
import com.sceneit.chris.sceneit.views.DrawableImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 27/02/2018.
 */

public class ImagePresenter implements ImageContract.IImagePresenter {

    private MainModel mainModel = MainModel.getInstance();

    private ImageFragment view;
    private ImageModel model;

    public ImagePresenter(ImageFragment view) {
        this.view = view;
        this.model = new ImageModel(this);
    }


    @Override
    public double getLatitude() {
        return this.model.getLat();
    }

    @Override
    public void setLatitude(double lat) {
        this.model.setLat(lat);
    }

    @Override
    public double getLongitude() {
        return this.model.getLng();
    }

    @Override
    public void setLongitude(double lon) {
        this.model.setLng(lon);
    }

    @Override
    public Paint getPaintSettings() {
        return this.model.getPaintSettings();
    }

    @Override
    public void setPaintSettings(Paint settings) {
        this.model.setPaintSettings(settings);
    }

    @Override
    public Bitmap getBitmap() {
        return this.model.getBitmap();
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        this.model.setBitmap(bitmap);
    }

    @Override
    public void setAndReplaceBitmap(Bitmap bitmap) {
        setBitmap(bitmap);
        this.model.setDrawableImage(new DrawableImage(view.getContext(), bitmap, this.model.getPaintSettings()));

        setContainerDrawView();
    }

    @Override
    public ConstraintLayout getImageContainer() {
        return this.model.getImageContainer();
    }

    @Override
    public void setImageContainer(ConstraintLayout container) {
        this.model.setImageContainer(container);
    }

    @Override
    public DrawableImage getDrawableImage() {
        return this.model.getDrawableImage();
    }

    @Override
    public void setDrawableImage(DrawableImage image) {
        this.model.setDrawableImage(image);
    }

    @Override
    public void setContainerDrawView() {
        Bitmap bitmap = this.model.getBitmap();
        ConstraintLayout container = this.model.getImageContainer();
        container.removeAllViews();
        container.addView(this.model.getDrawableImage());
        container.setMaxWidth(bitmap.getWidth());
        container.setMaxHeight(bitmap.getHeight());
    }

    @Override
    public Bitmap rotate(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();

        // left 1 right 3 up 0 down 0

        switch (orientation) {

            case 1:
                matrix.setRotate(-90);
                break;
            case 3:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void submitImage() {
        this.model.setSending(true);
        this.showProgress(true);

        this.model.getDrawableImage().disbale();

        final String uid = mainModel.getCurrentUser().getUid();
        final double lat = getLatitude();
        final double lng = getLongitude();
        final long time = System.currentTimeMillis();
        final String fileName = uid + "-" +
                getLatitude() + "_" +
                getLongitude() + "_" +
                System.currentTimeMillis() + ".jpg";


        StorageReference storageRef = mainModel.getStorageReference();
        StorageReference storageReference = storageRef.child("images/" + fileName);

        Bitmap image = this.model.getDrawableImage().getMutableBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Todo: handle fails
                StorageException ex = (StorageException) e;
                System.out.println(ex.getErrorCode());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                System.out.println(downloadUrl);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                submitImageInfo(uid, lat, lng, time, fileName);
            }
        });

    }

    @Override
    public void submitImageInfo(String uid, double lat, double lng, long time, String fileName) {
        List<Comments> comments =  new ArrayList<>();
        Map<String, String> votes = new HashMap<>();
        Image image = new Image(uid, lat, lng, fileName, new Date(time), 0, comments, votes);
        mainModel.getDataStore().collection(MainModel.IMAGE_COLLECTION).document().set(image)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        })
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteTempImage(view.getImagePath());
                model.setSending(false);
                showProgress(false);
                ((MainActivity) view.getActivity()).showFragment(HomeFragment.newInstance(), "HOME");
            }
        });
    }

    @Override
    public void deleteTempImage(String path) {
        File image = new File(Uri.parse(path).getPath());
        image.delete();
    }

    @Override
    public void closeImage() {
        ((MainActivity) view.getActivity()).showFragment(HomeFragment.newInstance(), "HOME");
    }

    @Override
    public boolean isSending() {
        return this.model.isSending();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        /**
         * Shows the progress UI and hides the login form.
         */
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = this.view.getResources().getInteger(android.R.integer.config_shortAnimTime);

            this.view.getProgress().setVisibility(show ? View.VISIBLE : View.GONE);
            this.view.getProgress().animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.getProgress().setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            this.view.getProgress().setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }

    }

    @Override
    public void toggleDraw() {

    }

    @Override
    public void setColour(int colour) {
        Paint paint = this.model.getPaintSettings();
        paint.setColor(colour);
        this.model.setPaintSettings(paint);
        this.model.getDrawableImage().setPaintOptions(paint);
    }
}
