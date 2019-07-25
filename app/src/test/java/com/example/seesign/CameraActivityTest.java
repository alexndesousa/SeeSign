package com.example.seesign;

import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CameraActivityTest {

    @Test
    public void toggleCalibration() {
    }

    @Test
    public void resizeImage() {
    }

    @Test
    public void convertBitmapToMat() {
    }

    @Test
    public void mergeImages() {
        CameraActivity cameraActivity = new CameraActivity();
        List<Bitmap> bitmaps = new ArrayList<>();
        for(int i=0; i<10; i++) {
            Bitmap bmp = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888);
            bitmaps.add(bmp);
        }
        Bitmap merged = cameraActivity.mergeImages(bitmaps);
        assertEquals(merged.getWidth(), 224*4);
        assertEquals(merged.getHeight(), 224*3);

    }
}