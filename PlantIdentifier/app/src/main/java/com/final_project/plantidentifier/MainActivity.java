package com.final_project.plantidentifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_camera;
    private Button btn_myPlants;
    private Button btn_search;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_myPlants = (Button) findViewById(R.id.btn_my_plants);
        btn_search = (Button) findViewById(R.id.btn_search);
    }

    public void onMyPlant(View v){
        Intent intent = new Intent(this, MyPlantsActivity.class);
        startActivity(intent);
    }

    public void onCamera(View v){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void onPreference(MenuItem menuItem){
        Intent intent = new Intent(this, PreferenceActivity.class);
        startActivity(intent);
    }

    public void onSearch(View v){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    public void onAbout(MenuItem menuItem){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void onContactUs(MenuItem menuItem){
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

}