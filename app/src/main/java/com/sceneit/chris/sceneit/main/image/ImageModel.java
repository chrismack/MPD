package com.sceneit.chris.sceneit.main.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;

import com.sceneit.chris.sceneit.views.DrawableImage;

/**
 * Created by Chris on 27/02/2018.
 */

public class ImageModel {

    private ImagePresenter presenter;

    private ConstraintLayout imageContainer;
    private DrawableImage drawableImage;
    private Paint paintSettings;
    private Bitmap bitmap;

    private double lat, lng;


    /*
     * Check if the image is currently being sent
     */
    private boolean sending;


    public ImageModel(ImagePresenter presenter) {
        this.presenter = presenter;

        // Setup default line colour
        this.paintSettings = new Paint();
        this.paintSettings.setAntiAlias(true);
        this.paintSettings.setDither(true);
        this.paintSettings.setColor(Color.BLACK);
        this.paintSettings.setStyle(Paint.Style.STROKE);
        this.paintSettings.setStrokeJoin(Paint.Join.ROUND);
        this.paintSettings.setStrokeCap(Paint.Cap.ROUND);
        this.paintSettings.setStrokeWidth(6);

        this.sending = false;
    }

    public Paint getPaintSettings() {
        return paintSettings;
    }

    public void setPaintSettings(Paint paintSettings) {
        this.paintSettings = paintSettings;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ConstraintLayout getImageContainer() {
        return imageContainer;
    }

    public void setImageContainer(ConstraintLayout imageContainer) {
        this.imageContainer = imageContainer;
    }

    public DrawableImage getDrawableImage() {
        return drawableImage;
    }

    public void setDrawableImage(DrawableImage drawableImage) {
        this.drawableImage = drawableImage;
    }

    public boolean isSending() {
        return sending;
    }

    public void setSending(boolean sending) {
        this.sending = sending;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


}
