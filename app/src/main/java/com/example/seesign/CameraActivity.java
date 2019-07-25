package com.example.seesign;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.Utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import static org.opencv.core.Core.ROTATE_90_CLOCKWISE;

public class CameraActivity extends NavigationDrawer implements CvCameraViewListener2 {
    private static final String TAG = "CameraActivity";

    private CameraView mOpenCvCameraView;
    private Mat mImage;
    private Mat mImageNoThreshold;
    private Button mCaptureButton;
    private Button mThresholdButton;
    private Button mFlashButton;
    private Boolean threshold = false;
    private Scalar mMinThreshold;
    private Scalar mMaxThreshold;
    private Translator mTranslator;
    private Bitmap mImageBitmap;

    private Boolean calibrationVisible = false;
    private SeekBar mSeekCr;
    private SeekBar mSeekCb;
    private Integer averageCr = 170;
    private Integer averageCb = 100;

    private List<Bitmap> mBitmapList = new ArrayList<>();
    private List<String> mStringList = new ArrayList<>();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraActivity() {
    }

    /** Called when the activity is created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //following statement accessed sharedPreferences and decides if the tutorial should be shown
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (!sharedPreferences.getBoolean("firstLaunch", false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstLaunch", true);
            editor.apply();
            Intent intent = new Intent(this, TutorialActivity.class); // Call the AppIntro java class
            startActivity(intent);
        }

        //if it doesnt have the permissions, get them
        if (checkPermissions()) {
            requestPermissions();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setTitle("Camera");

        FrameLayout content = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.camera_surface_view, content);

        mOpenCvCameraView = findViewById(R.id.camera_activity_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mCaptureButton = findViewById(R.id.camera_capture_button);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        mFlashButton = findViewById(R.id.camera_flash_button);
        mFlashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenCvCameraView.toggleFlash();
            }
        });

        mThresholdButton = findViewById(R.id.threshold_toggle_button);
        mThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threshold = !threshold;
            }
        });
        mThresholdButton.setVisibility(View.INVISIBLE);

        mSeekCb = findViewById(R.id.seekbarCb);
        mSeekCb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                averageCb = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Make it so the seek bars arent visible by default
        mSeekCb.setVisibility(View.INVISIBLE);

        mSeekCr = findViewById(R.id.seekbarCr);
        mSeekCr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                averageCr = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Make it so the seek bars arent visible by default
        mSeekCr.setVisibility(View.INVISIBLE);

        //Initialise the translator
        try {
            mTranslator = new Translator(this);
            System.out.println("successfully created classifier");
        } catch (IOException e) {
            System.out.println("problem with creating classifier");
        }
    }

    /** Used to create the options menu at the top right of the screen*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }

    /** Used to handle when an item in the options menu is selected. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calibration_settings:
                toggleCalibration();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Called when the activity is paused. */
    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        // handles the reading and storing of the translation image and result pairs
        if (mBitmapList.size() != 0 || mStringList.size() != 0) {
            Bitmap combinedGestures = mergeImages(mBitmapList);
            Mat mat = convertBitmapToMat(combinedGestures);
            String translation = String.join(", ", mStringList).replace(", ","");
            String imageLocation = saveImage(mat);

            List<String> pairs = new ArrayList<>();

            //if translation_pairs already exists load it into an arraylist
            try {
                FileInputStream file = openFileInput("translation_pairs");
                BufferedReader reader = new BufferedReader(new InputStreamReader(file));
                String line = reader.readLine();
                while(line != null) {
                    pairs.add(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (Exception e) {
                System.out.println("translation_pairs doesn't exist");
            }

            //create a new text file to store the translation pairs
            String filename = "translation_pairs";
            String fileContents = translation+":"+imageLocation+"\n";
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                //if pairs array is populated, copy the data into this translation_pairs.
                //this is done to make it function like a queue (First in first out)
                if (pairs.size() > 0) {
                    for(int i = 0; i<pairs.size(); i++) {
                        outputStream.write((pairs.get(i)+"\n").getBytes());
                    }
                }
                outputStream.close();
            } catch (Exception e) {
                System.out.println("error if file doesnt exist");
            }
        }
    }

    /** Called when the activity is resumed. */
    @Override
    public void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV library loaded successfully!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV library not found! Try using OpenCV Manager");
        }


        // clear the contents of these lists
        mBitmapList.clear();
        mStringList.clear();
    }

    /** Called when the activity is destroyed. */
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    /** Called every frame. This is used for the camera preview. */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //The thresholds must be applied to the preview
        mMinThreshold = new Scalar(80, averageCr-20, averageCb-20);
        mMaxThreshold = new Scalar(255, averageCr+20, averageCb+20);
        //The preview without any effects needs to be stored to be used on translations activity
        mImageNoThreshold = inputFrame.rgba().clone();
        mImage = inputFrame.rgba();
        if (threshold) {
            mImage = YCrCb_threshold(mImage, mMinThreshold, mMaxThreshold);
        }
        return mImage;
    }

    /** Checks if application has access to both camera and write permissions */
    private boolean checkPermissions() {
        return (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    /** Requests both camera and write permissions */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    /** Toggles the calibration settings by making the sliders and the threshold button
     * visible/invisible. */
    public void toggleCalibration() {
        if (calibrationVisible) {
            mSeekCr.setVisibility(View.INVISIBLE);
            mSeekCb.setVisibility(View.INVISIBLE);
            mThresholdButton.setVisibility(View.INVISIBLE);
            calibrationVisible = false;
        } else {
            mSeekCr.setVisibility(View.VISIBLE);
            mSeekCb.setVisibility(View.VISIBLE);
            mThresholdButton.setVisibility(View.VISIBLE);
            calibrationVisible = true;
        }
    }

    /** Used to convert a Mat object to the YCrCb colour space then threshold it.
     * Mat mat : input Mat object to be converted and thresholded.
     * Scalar min : scalar dictating the lower threshold bound.
     * Scalar max : scalar dictating the upper threshold bound. */
    private Mat YCrCb_threshold(Mat mat, Scalar min, Scalar max) {
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2YCrCb);
        Core.inRange(mat, min, max, mat);
        return mat;
    }

    /** Rotates a Mat object 90 degrees clockwise. */
    private Mat rotateImage(Mat mat) {
        org.opencv.core.Core.rotate(mat, mat, ROTATE_90_CLOCKWISE);
        mImageNoThreshold = mat;
        return mat;
    }

    /** Resizes a Mat object to be 224*224 pixels. */
    public Mat resizeImage(Mat mat) {
        Mat newImage = new Mat();
        org.opencv.core.Size size = new Size(224,224);
        Imgproc.resize(mat, newImage, size);
        return newImage;
    }

    /** Saves a Mat object as a png with the current date and time in its name. */
    private String saveImage(Mat mat) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateAndTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                "/seeseign_picture_" + currentDateAndTime + ".png";
        Imgcodecs.imwrite(fileName, mat);
        return fileName;
    }

    /** Takes a picture from the camera preview and manipulates it. */
    private void takePicture() {
        Mat rotatedImage = rotateImage(mImageNoThreshold);
        Mat resizedImage = resizeImage(rotatedImage);

        //openCV operates in BGR instead of RGB. This makes the images on the translation page
        // appear in RGB instead
        Mat recolouredImage = new Mat();
        Imgproc.cvtColor(resizedImage, recolouredImage, Imgproc.COLOR_BGR2RGB);
        mBitmapList.add(convertMatToBitmap(recolouredImage));

        Mat thresholdedImage = YCrCb_threshold(resizedImage, mMinThreshold, mMaxThreshold);
        mImageBitmap = convertMatToBitmap(thresholdedImage);

        String result = mTranslator.translate(mImageBitmap);

        mStringList.add(result);

        Toast translationComplete = Toast.makeText(this,
                "Gesture successfully translated.", Toast.LENGTH_SHORT);
        translationComplete.setGravity(Gravity.CENTER_VERTICAL,0,110);
        translationComplete.show();
    }

    /** Converts a Mat object to a Bitmap. */
    public Bitmap convertMatToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    /** Converts a Bitmap image to a Mat object. */
    public Mat convertBitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        return mat;
    }

    /** Merges multiple bitmaps into one image.
     * Images are merged row by row then the rows are merged vertically.     *
     * List<Bitmap> bitmaps : used to provide the bitmaps to be merged. */
    public Bitmap mergeImages(List<Bitmap> bitmaps) {
        int maxImagesPerRow = 4;
        int w = 224*maxImagesPerRow, h = 224;
        //used to determine how many rows of 4 there are.
        int quotient = bitmaps.size() / maxImagesPerRow;
        //used to determine the remaining images
        int remainder = bitmaps.size() % maxImagesPerRow;
        Bitmap cs = bitmaps.get(0);
        //keeps track of the merged rows
        List<Bitmap> completedRows = new ArrayList<>();
        if (quotient > 0) {
            for (int i = 0; i < quotient; i++) {
                cs = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas combined = new Canvas(cs);
                for (int j = 0; j < maxImagesPerRow; j++) {
                    combined.drawBitmap(bitmaps.get(0), 224*j, 0f, null);
                    bitmaps.remove(0);
                }
                completedRows.add(cs);
            }
        }
        if (remainder > 0) {
            cs = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas combined = new Canvas(cs);
            for (int i = 0; i<remainder; i++) {
                combined.drawBitmap(bitmaps.get(0), 224*i, 0f, null);
                bitmaps.remove(0);
            }
            completedRows.add(cs);
        }
        //statement merges the rows one above the other
        if (completedRows.size() > 1) {
            h = 224 * completedRows.size();
            cs = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas combined = new Canvas(cs);
            for (int i = 0; i < completedRows.size()+1; i++) {
                combined.drawBitmap(completedRows.get(0), 0f, 224*i, null);
            }
        }
        //used to give the image transparency
        cs.setHasAlpha(true);
        return cs;
    }
}
