package com.sceneit.chris.sceneit.main.image;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;

import com.sceneit.chris.sceneit.views.DrawableImage;

/**
 * Created by Chris on 27/02/2018.
 */

public interface ImageContract {

    interface IImagePresenter {
        Paint getPaintSettings();

        void setPaintSettings(Paint settings);

        Bitmap getBitmap();

        void setBitmap(Bitmap bitmap);

       void setAndReplaceBitmap(Bitmap bitmap);

        ConstraintLayout getImageContainer();

        void setImageContainer(ConstraintLayout container);

        DrawableImage getDrawableImage();

        void setDrawableImage(DrawableImage image);
    }

    interface IImageView {

    }
}
