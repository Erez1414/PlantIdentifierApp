package com.final_project.plantidentifier.dataUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.final_project.plantidentifier.AboutActivity;
import com.final_project.plantidentifier.CameraActivity;
import com.final_project.plantidentifier.ContactActivity;
import com.final_project.plantidentifier.MainActivity;
import com.final_project.plantidentifier.PreferenceActivity;
import com.final_project.plantidentifier.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

public class FlowerInfoPage extends AppCompatActivity {

    private TextView mTvName;
    private TextView mTvDesc;
    private TextView mTvWater;
    private TextView mTvSun;
    private TextView mTvSoil;
    private ImageView mIvFlower;
    private FloatingActionButton fabButton;

    JSONObject plantInfo = null;

    private Bitmap mImg;
    private final String TAG = "FlowerInfoPageTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_info_page);

        mTvName = (TextView) findViewById(R.id.tv_flower_name);
        mTvDesc = (TextView) findViewById(R.id.tv_flower_desc);
        mTvWater = (TextView) findViewById(R.id.tv_water_info);
        mTvSun = (TextView) findViewById(R.id.tv_sun_info);
        mTvSoil = (TextView) findViewById(R.id.tv_soil_info);
        mIvFlower = (ImageView) findViewById(R.id.iv_flower_img);

        fillInfo();

        fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addIntent = new Intent(FlowerInfoPage.this, AddPlantActivity.class);
                Log.d(TAG, "in flower info page !!!!!!!!!!!!!!!!! created intent");
                double[] location = getIntent().getDoubleArrayExtra("location");
                if (location == null)
                    location = new double[] {-33.8523341, 151.2106085}; // default place in (Sydney, Australia)
                addIntent.putExtra(String.valueOf(R.string.key), mTvName.getText());
                addIntent.putExtra("INFO", plantInfo.toString());
                addIntent.putExtra("img", mImg);
                addIntent.putExtra("location", location);
                fabButton.setVisibility(View.INVISIBLE);
                startActivity(addIntent);
            }
        });
    }

    private int getDefaultImg(String type) {
        switch (type){
            case "Daisy":
                return R.mipmap.ic_daisy_foreground;
            case "Rose":
                return R.mipmap.ic_rose_foreground;
            case "Dandelion":
                return R.mipmap.ic_dandelion_foreground;
            case "Sunflower":
                return R.mipmap.ic_sunflower_foreground;
            case "Tulip":
                return R.mipmap.ic_tulip_foreground;
            default:
                return R.mipmap.ic_flower_foreground;
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

    private void fillInfo(){
        Intent i = getIntent();
        try {
            plantInfo = new JSONObject(i.getStringExtra("INFO"));
        }
        catch (Exception e){
            Log.d(TAG, e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        try {
            String nametype = plantInfo.getString("type").substring(0, 1).toUpperCase() +
                    plantInfo.getString("type").substring(1);
            mTvName.setText(nametype);
            mTvDesc.setText(plantInfo.getString("desc").trim()); //.replace(" ", "\u00A0"));
            mTvWater.setText(plantInfo.getString("water").trim());
            mTvSun.setText(plantInfo.getString("sun").trim());
            mTvSoil.setText(plantInfo.getString("soil").trim());
        } catch (JSONException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        try {
            mImg = i.getParcelableExtra("img");
            mIvFlower.setImageBitmap(Bitmap.createScaledBitmap(mImg, 250, 250, true));
        } catch (Exception ignored){
            mImg = BitmapFactory.decodeResource(getResources(), getDefaultImg(mTvName.getText().toString()));
            mIvFlower.setImageBitmap(Bitmap.createScaledBitmap(mImg, 250, 250, true));
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}