package com.example.seesign;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Translator {
    private static final String MODEL_PATH = "model.tflite";

    private static final int BATCH_SIZE = 1;
    public static final int IMG_HEIGHT = 224;
    public static final int IMG_WIDTH = 224;
    private static final int DEPTH = 1;
    private static final int NUMBER_OF_CATEGORIES = 24;

    private final Interpreter mInterpreter;
    private final ByteBuffer mImgData;

    private final float[][] mResult = new float[1][NUMBER_OF_CATEGORIES];
    private final String[] mLabels = {"A", "B", "C", "D", "E", "F", "G", "I",
                                      "K", "L", "M", "N", "O", "P", "Q", "R",
                                      "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /** Constructor
     * Activity activity : activity this is called from */
    public Translator(Activity activity) throws IOException {
        mInterpreter = new Interpreter(loadModelFile(activity));

        //create a new bytebuffer of the correct size
        mImgData = ByteBuffer.allocateDirect(
                4 * BATCH_SIZE * IMG_HEIGHT * IMG_WIDTH * DEPTH);
        mImgData.order(ByteOrder.nativeOrder());
    }

    /** Predicts the correct label for a given image
     * Bitmap bitmap : image to be translated
     *
     * returns: String of the correct label */
    public String translate(Bitmap bitmap) {
        convertBitmapToByteBuffer(bitmap);
        mInterpreter.run(mImgData, mResult);
        float max = Max(mResult[0]);
        int index = index(mResult[0], max);
        return mLabels[index];
    }

    /** Selects the largest float in an array of floats. Used to select the highest prediction.
     * float[] floats : array of floats
     *
     * returns: Float of the most accurate prediction */
    public float Max(float[] floats) {
        float max = floats[0];
        for(int i=1; i<floats.length; i++) {
            if (max < floats[i]) {
                max = floats[i];
            }
        }
        return max;
    }

    /** Gets the index of a given float in an array of floats
     * float[] floats : array of floats
     * float num : number to be searched for
     *
     * returns: Int index of the given float num in the array floats.*/
    public int index(float[] floats, float num) {
        for (int i = 0; i<floats.length; i++) {
            if(Math.abs(floats[i] - num) < 0.0001) {
                return i;
            }
        }
        return 0;
    }

    /** Used to load the TFLite model file into memory
     * Activity activity : activity this is called from
     *
     * returns: MappedByteBuffer of the loaded TFLite model */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /** Used to convert a Bitmap to a ByteBuffer to be used with the model.
     * Bitmap bitmap : image to be converted. */
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (mImgData == null) {
            return;
        }
        mImgData.rewind();
        int[] mImagePixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(mImagePixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < IMG_WIDTH; ++i) {
            for (int j = 0; j < IMG_HEIGHT; ++j) {
                final int val = mImagePixels[pixel++];
                mImgData.putFloat(convertToGreyScale(val));
            }
        }
    }

    /** Converts RGB pixel values to greyscale
     * int colour : pixel value to be converted.
     *
     * returns: float greyscale pixel value. */
    private float convertToGreyScale(int colour) {
        return (((colour >> 16) & 0xFF) + ((colour >> 8) & 0xFF) + (colour & 0xFF)) / 3.0f;
    }
}
