package com.example.plantidentifier.dataUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantidentifier.AboutActivity;
import com.example.plantidentifier.CameraActivity;
import com.example.plantidentifier.ContactActivity;
import com.example.plantidentifier.MyPlantsActivity;
import com.example.plantidentifier.R;
import com.example.plantidentifier.data.AppDatabase;
import com.example.plantidentifier.data.PlantEntry;
import com.example.plantidentifier.utils.NotificationReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class MyFlowerInfo extends AppCompatActivity{

    private static final String TAG = "MyFlowerInfo";
    private TextView mTvName;
    private TextView mTvType;
    private TextView mTvLoc;
    private TextView mTvDesc;
    private TextView mTvWater;
    private TextView mTvSun;
    private TextView mTvSoil;
    private ImageView mIvFlower;
    private int mAlarm;
    private FloatingActionButton mBtn;
    private Drawable notificationsOn;
    private Drawable notificationsOff;

    private AppDatabase mDb;
    private String name;
    private String type;
    private PlantEntry plantEntry;

    public PendingIntent pendingIntent;
    public AlarmManager alarmManager;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public final static String default_notification_channel_id = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_flower_info);

        Log.d(TAG, "in my flower info page!!!!");
        mTvName = (TextView) findViewById(R.id.tv_my_flower_name);
        mTvType = (TextView) findViewById(R.id.tv_my_flower_type);
        mTvLoc = (TextView) findViewById(R.id.tv_my_flower_loc);
        mTvDesc = (TextView) findViewById(R.id.tv_my_flower_desc);
        mTvWater = (TextView) findViewById(R.id.tv_my_water_info);
        mTvSun = (TextView) findViewById(R.id.tv_my_sun_info);
        mTvSoil = (TextView) findViewById(R.id.tv_my_soil_info);
        mIvFlower = (ImageView) findViewById(R.id.iv_my_flower_img);
        mBtn = (FloatingActionButton) findViewById(R.id.btn_create_notification);

        Resources res = this.getResources();
        notificationsOff = ResourcesCompat.getDrawable(res, R.drawable.ic_notifications_off, null);
        notificationsOn = ResourcesCompat.getDrawable(res, R.drawable.ic_notifications_on, null);

        //init thr database object
        mDb = AppDatabase.getInstance(getApplicationContext());
        showFlower();
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

    private void fillInfo(){
        mIvFlower.setImageBitmap(Bitmap.createScaledBitmap(plantEntry.getImg(), 250, 250, true));
        name = plantEntry.getName();
        type = plantEntry.getType().substring(0, 1).toUpperCase() + plantEntry.getType().substring(1);
        mTvName.setText(name);
        mTvType.setText(type);
        String loc = "Located at: " + plantEntry.getLoc();
        mTvLoc.setText(loc);
        mTvDesc.setText(plantEntry.getDesc().trim());
        mTvWater.setText(plantEntry.getWater().trim());
        mTvSun.setText(plantEntry.getSun().trim());
        mTvSoil.setText(plantEntry.getSoil().trim());
        mAlarm = plantEntry.getAlarm();

        if (plantEntry.isNotification()){
            mBtn.setImageDrawable(notificationsOn);
        } else {
            mBtn.setImageDrawable(notificationsOff);
        }
    }

    /*
    gets the correct row from myFlower database to fill info in the page
     */
    private void showFlower(){
        Intent i = getIntent();
        int id = i.getIntExtra("id", 0);
        plantEntry = mDb.plantDao().getPlantById(id);
        fillInfo();
    }

    /*
    creates a watering notification by pending intent to notificationReceiver
     */
    private void scheduleNotification (Notification notification) {
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("notification", notification);
        intent.putExtra("id", plantEntry.getId());
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        long interval = AlarmManager.INTERVAL_DAY * (7 / mAlarm); // 1000 * 60 * 5   -> 5min

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int randomMin = ThreadLocalRandom.current().nextInt(0, 60);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, randomMin);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                interval, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                interval, pendingIntent);
    }

    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle("Time to water your plant!");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
    }

    private void addNotification(){
        String notificationContent = name + " (" + type + ") " + mTvLoc.getText();

        //this part was supposed to make the notifications survive a restart of the device
        Context context = this;
        ComponentName receiver = new ComponentName(context, NotificationReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        scheduleNotification(getNotification(notificationContent));
        Toast.makeText(this, "notifications created successfully", Toast.LENGTH_LONG).show();
    }

    private void removeNotification(){
        scheduleNotification(getNotification(""));
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "notifications removed successfully", Toast.LENGTH_LONG).show();
    }


    /*
    adds or removes repeated notifications when clicking the button
     */
    public void onSetNotifications(View view) {
        if (plantEntry.isNotification()){
            removeNotification();
            plantEntry.setNotification(false);
            mDb.plantDao().updateTask(plantEntry);
            mBtn.setImageDrawable(notificationsOff);
        } else {
            addNotification();
            plantEntry.setNotification(true);
            mDb.plantDao().updateTask(plantEntry);
            mBtn.setImageDrawable(notificationsOn);
        }
    }

    public void onShowMap(View view){
        double[] imgLocation = plantEntry.getImgLocation();
        String geoUri = "http://maps.google.com/maps?q=loc:" + (float)imgLocation[0] + "," + (float)imgLocation[1] + " (this is where you found " + name + ")";
//        String geoUri = String.format(Locale.ENGLISH,"geo:%f,%f",(float)imgLocation[0],(float)imgLocation[1] );
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setData(Uri.parse(geoUri));
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        } else {
            Toast.makeText(this, "Could not find an app that supports map.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MyPlantsActivity.class);
        startActivity(intent);
    }
}