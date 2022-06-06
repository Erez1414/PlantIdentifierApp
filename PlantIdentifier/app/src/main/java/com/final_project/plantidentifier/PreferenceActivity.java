package com.final_project.plantidentifier;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

public class PreferenceActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch tutorialSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        tutorialSwitch = (Switch) findViewById(R.id.s_tutorial_pref);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initPref();
    }

    private void initPref(){
        sharedPreferences = this.getSharedPreferences("com.final_project.plantidentifier", Context.MODE_PRIVATE);
        Boolean curPref = sharedPreferences.getBoolean("tutorial", true);
        tutorialSwitch.setChecked(curPref);
    }

    public void onTutorialModeChange(View v){
        sharedPreferences = this.getSharedPreferences("com.final_project.plantidentifier", Context.MODE_PRIVATE);
        Boolean curPref = sharedPreferences.getBoolean("tutorial", true);
        sharedPreferences.edit().putBoolean("tutorial", !curPref).apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}