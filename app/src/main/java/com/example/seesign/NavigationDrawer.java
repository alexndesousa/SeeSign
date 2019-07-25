package com.example.seesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class NavigationDrawer extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nv);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //binding relevant activity to each button in navigation drawer
                switch (item.getItemId()) {
                    case R.id.camera:
                        Intent intent = new Intent(getApplicationContext(),
                                CameraActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.translations:
                        Intent translationIntent = new Intent(getApplicationContext(),
                                PreviousTranslationsActivity.class);
                        startActivity(translationIntent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.supported:
                        Intent supportedIntent = new Intent(getApplicationContext(),
                                SupportedGesturesActivity.class);
                        startActivity(supportedIntent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.help:
                        Intent helpIntent = new Intent(getApplicationContext(),
                               HelpActivity.class);
                        startActivity(helpIntent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.tutorial:
                        Intent tutorialIntent = new Intent(getApplicationContext(),
                                TutorialActivity.class);
                        startActivity(tutorialIntent);
                        drawerLayout.closeDrawers();
                        break;

                }
                return false;
            }
        });

        actionBarDrawerToggle.syncState();
    }
}
