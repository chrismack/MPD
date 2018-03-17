package com.sceneit.chris.sceneit.main.image;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.sceneit.chris.sceneit.views.DrawableImage;

/**
 * Created by Chris on 27/02/2018.
 */

public interface ImageContract {

    interface IImagePresenter {

        double getLatitude();

        void setLatitude(double lat);

        double getLongitude();

        void setLongitude(double lon);

        Paint getPaintSettings();

        void setPaintSettings(Paint settings);

        Bitmap getBitmap();

        void setBitmap(Bitmap bitmap);

       void setAndReplaceBitmap(Bitmap bitmap);

        ConstraintLayout getImageContainer();

        void setImageContainer(ConstraintLayout container);

        DrawableImage getDrawableImage();

        void setDrawableImage(DrawableImage image);

        void setContainerDrawView();

        Bitmap rotate(Bitmap bitmap, int orientation);

        void submitImage();

        void submitImageInfo(String uid, double lat, double lng, long time, String fileName);

        void deleteTempImage(String imagePath);

        void closeImage();

        boolean isSending();

        void showProgress(boolean show);

        void toggleDraw();

        void setColour(int colour);
    }

    interface IImageView {
        void onSend();

        void onClose();

        void onToggleDraw();

        View getProgress();

        String getImagePath();
    }
}
