package com.example.seesign;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class SupportedGesturesActivity extends NavigationDrawer {

    private List<Gesture> gestureList = new ArrayList<>();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Supported gestures");
        FrameLayout content = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_previous_translations, content);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //populate previous translations page
        gestureList.add(new Gesture(R.drawable.a, "A"));
        gestureList.add(new Gesture(R.drawable.b, "B"));
        gestureList.add(new Gesture(R.drawable.c, "C"));
        gestureList.add(new Gesture(R.drawable.d, "D"));
        gestureList.add(new Gesture(R.drawable.e, "E"));
        gestureList.add(new Gesture(R.drawable.f, "F"));
        gestureList.add(new Gesture(R.drawable.g, "G"));
        gestureList.add(new Gesture(R.drawable.i, "I"));
        gestureList.add(new Gesture(R.drawable.k, "K"));
        gestureList.add(new Gesture(R.drawable.l, "L"));
        gestureList.add(new Gesture(R.drawable.m, "M"));
        gestureList.add(new Gesture(R.drawable.n, "N"));
        gestureList.add(new Gesture(R.drawable.o, "O"));
        gestureList.add(new Gesture(R.drawable.p, "P"));
        gestureList.add(new Gesture(R.drawable.q, "Q"));
        gestureList.add(new Gesture(R.drawable.r, "R"));
        gestureList.add(new Gesture(R.drawable.s, "S"));
        gestureList.add(new Gesture(R.drawable.t, "T"));
        gestureList.add(new Gesture(R.drawable.u, "U"));
        gestureList.add(new Gesture(R.drawable.v, "V"));
        gestureList.add(new Gesture(R.drawable.w, "W"));
        gestureList.add(new Gesture(R.drawable.x, "X"));
        gestureList.add(new Gesture(R.drawable.y, "Y"));
        gestureList.add(new Gesture(R.drawable.z, "Z"));

        GestureAdapter adapter = new GestureAdapter(this, gestureList, R.layout.gesture_layout);

        recyclerView.setAdapter(adapter);
    }
}
