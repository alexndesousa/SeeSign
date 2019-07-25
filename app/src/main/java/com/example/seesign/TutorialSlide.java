package com.example.seesign;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialSlide extends Fragment {

    private static final String LAYOUT_ID = "layout";
    private int layoutId;

    public TutorialSlide() {

    }

    public static TutorialSlide newInstance(int layout) {
        TutorialSlide slide = new TutorialSlide();

        Bundle bundle= new Bundle();
        bundle.putInt(LAYOUT_ID, layout);
        slide.setArguments(bundle);

        return slide;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutId = getArguments().getInt(LAYOUT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle savedInstanceState) {
        return inflater.inflate(layoutId, view, false);
    }
}
