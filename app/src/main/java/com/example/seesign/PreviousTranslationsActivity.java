package com.example.seesign;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PreviousTranslationsActivity extends NavigationDrawer {

    private List<Gesture> gestureList = new ArrayList<>();

    private RecyclerView recyclerView;

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Previous translations");
        FrameLayout content = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_previous_translations, content);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //load in the items from the text file
        try {
            FileInputStream file = openFileInput("translation_pairs");
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while(line != null) {
                String[] pair = line.split(":");
                String translation = pair[0];
                String location = pair[1];
                File imgFile = new File(location);
                Bitmap gesture = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                gestureList.add(new Gesture(gesture, translation));
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("problem with loading in data");
        }


        GestureAdapter adapter = new GestureAdapter(this, gestureList, R.layout.gesture_layout);

        recyclerView.setAdapter(adapter);
    }

}
