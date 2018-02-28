package com.sceneit.chris.sceneit.main.image;


import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;

import com.sceneit.chris.sceneit.views.DrawableImage;

/**
 * Created by Chris on 27/02/2018.
 */

public class ImagePresenter implements ImageContract.IImagePresenter {

    private ImageFragment view;
    private ImageModel model;

    public ImagePresenter(ImageFragment view) {
        this.view = view;
        this.model = new ImageModel(this);
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

        ConstraintLayout container = this.model.getImageContainer();
        container.removeAllViews();
        container.addView(this.model.getDrawableImage());
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
}
