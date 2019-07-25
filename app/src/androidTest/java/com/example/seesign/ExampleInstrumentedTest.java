package com.example.seesign;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.seesign", appContext.getPackageName());
        System.out.println("hi");
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
