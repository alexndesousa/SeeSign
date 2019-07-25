package com.example.seesign;

import android.graphics.Bitmap;

public class Gesture {

    private Bitmap mImage;
    private boolean isBitmap;
    private int mImageDrawable;
    private String description;

    /** Constructor
     * Bitmap image : image of gesture.
     * String desc : translation for the given image */
    public Gesture(Bitmap image, String desc) {
        this.mImage = image;
        this.description = desc;
        this.isBitmap = true;
    }

    /** Constructor
     * int mImageDrawable : reference to the image of a gesture.
     * String desc : translation for the given image */
    public Gesture(int mImageDrawable, String desc) {
        this.mImageDrawable = mImageDrawable;
        this.description = desc;
        this.isBitmap = false;
    }

    /** Getters and setters */

    public boolean isBitmap() { return isBitmap;}

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap mImage) {
        this.mImage = mImage;
    }

    public int getImageDrawable() {
        return mImageDrawable;
    }

    public void setImageDrawable(int mImage) {
        this.mImageDrawable = mImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
