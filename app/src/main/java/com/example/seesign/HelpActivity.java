package com.example.seesign;

import android.os.Bundle;
import android.widget.FrameLayout;

public class HelpActivity extends NavigationDrawer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Help");
        FrameLayout content = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.help_layout, content);
    }

}
