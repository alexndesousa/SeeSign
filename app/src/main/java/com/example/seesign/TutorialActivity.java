package com.example.seesign;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

public class TutorialActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add slides to the tutorial screens
        addSlide(TutorialSlide.newInstance(R.layout.first_tutorial_slide));
        addSlide(TutorialSlide.newInstance(R.layout.second_tutorial_slide));
        addSlide(TutorialSlide.newInstance(R.layout.third_tutorial_slide));
        addSlide(TutorialSlide.newInstance(R.layout.fourth_tutorial_slide));
        addSlide(TutorialSlide.newInstance(R.layout.fifth_tutorial_slide));

        showSkipButton(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        startActivity(intent);
    }
}
