package com.final_project.plantidentifier.dataUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.final_project.plantidentifier.AboutActivity;
import com.final_project.plantidentifier.ContactActivity;
import com.final_project.plantidentifier.MainActivity;
import com.final_project.plantidentifier.PreferenceActivity;
import com.final_project.plantidentifier.R;
import com.final_project.plantidentifier.data.AppDatabase;
import com.final_project.plantidentifier.data.PlantEntry;

import org.json.JSONObject;

import java.util.Date;

public class AddPlantActivity extends AppCompatActivity {

    private static final String TAG = AddPlantActivity.class.getSimpleName();

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_PLANT_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_PLANT_ID = "instanceTaskId";
    // Constants for priority
    private static final int DEFAULT_PLANT_ID = -1;

    EditText mEtName;
    EditText mEtLoc;
    String mKey;
    Button mBtn;
    Bitmap mImg;

    private JSONObject plantInfo;
    private int mPlantId = DEFAULT_PLANT_ID;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        Log.d(TAG, "in Add plNT popup !!!!!!!!!!!");
        mEtName = (EditText) findViewById(R.id.et_add_name);
        mEtLoc = (EditText) findViewById(R.id.et_add_loc);
        mBtn = (Button) findViewById(R.id.btn_add_plant_popup);

        Intent intent = getIntent();
        mKey = intent.getStringExtra(String.valueOf(R.string.key));
        try {
            mImg = intent.getParcelableExtra("img");
            plantInfo = new JSONObject(intent.getStringExtra("INFO"));
        } catch (Exception ignored){}

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_PLANT_ID)) {
            mPlantId = savedInstanceState.getInt(INSTANCE_PLANT_ID, DEFAULT_PLANT_ID);
        }

        //todo: check if this does anything
        if (intent.hasExtra(EXTRA_PLANT_ID) && intent.hasExtra(String.valueOf(R.string.key))) {
            mBtn.setText(R.string.update_button);

            if (mPlantId == DEFAULT_PLANT_ID) {
                // populate the UI
            }
        }
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

    public void onPreference(MenuItem menuItem){
        Intent intent = new Intent(this, PreferenceActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_PLANT_ID, mPlantId);
        super.onSaveInstanceState(outState);
    }

    private void populateUI(PlantEntry task) {

    }


    public void onClickAdd(View view){
        String name = mEtName.getText().toString();
        String loc = mEtLoc.getText().toString();
        if(name.matches("")){
            Toast.makeText(this, R.string.err_name, Toast.LENGTH_LONG).show();
            return;
        }
        if(loc.matches("")){
            Toast.makeText(this, R.string.err_location, Toast.LENGTH_LONG).show();
            return;
        }
        Date date = new Date();
        String type = "", desc ="", water ="", sun ="", soil = "";
        int alarm = 1;
        try {
            type = plantInfo.getString("type");
            desc = plantInfo.getString("desc");
            water = plantInfo.getString("water");
            sun = plantInfo.getString("sun");
            soil = plantInfo.getString("soil");
            alarm = plantInfo.getInt("alarm");
        } catch (Exception ignored){}
        double[] imgLocation = getIntent().getDoubleArrayExtra("location");
        final PlantEntry plantEntry = new PlantEntry(type, name, loc, desc, water, sun, soil, alarm, false, date, mImg, imgLocation);
        Log.d(TAG, "created an entry!");
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.plantDao().insertTask(plantEntry);
                finish();
            }
        });
        Log.d(TAG, "added entry to database!" + name);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FlowerInfoPage.class);
        Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!WW!!!!!!!!!!!!!!!!!!!!");
        intent.putExtra("INFO", plantInfo.toString());
        intent.putExtra("img", mImg);
        startActivity(intent);
    }
}