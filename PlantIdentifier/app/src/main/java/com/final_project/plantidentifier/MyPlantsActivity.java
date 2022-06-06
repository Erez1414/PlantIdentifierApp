package com.final_project.plantidentifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.final_project.plantidentifier.data.AppDatabase;
import com.final_project.plantidentifier.data.PlantEntry;
import com.final_project.plantidentifier.dataUtils.AppExecutors;
import com.final_project.plantidentifier.dataUtils.FlowerAdapter;
import com.final_project.plantidentifier.dataUtils.MyFlowerInfo;
import com.final_project.plantidentifier.utils.NotificationReceiver;

import java.util.Calendar;
import java.util.List;
import static com.final_project.plantidentifier.dataUtils.MyFlowerInfo.default_notification_channel_id;

public class MyPlantsActivity extends AppCompatActivity implements FlowerAdapter.ItemClickListener{

    private static final String TAG = MyPlantsActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;

    private FlowerAdapter mFlowerAdapter;
    private TextView mTvNoSaved;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plants);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_flower);
        mTvNoSaved = (TextView) findViewById(R.id.tv_no_saved_flowers);

        mTvNoSaved.setVisibility(View.GONE);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFlowerAdapter = new FlowerAdapter(this, this);
        mRecyclerView.setAdapter(mFlowerAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // COMPLETED (3) get the position from the viewHolder parameter
                        int position = viewHolder.getAdapterPosition();
                        List<PlantEntry> tasks = mFlowerAdapter.getPlants();
                        // COMPLETED (4) Call deleteTask in the taskDao with the task at that position

                        int id = tasks.get(position).getId();
                        removeNotification(id);

                        mDb.plantDao().deleteTask(tasks.get(position));
                        // COMPLETED (6) Call retrieveTasks method to refresh the UI
                        retrievePlants();
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        mDb = AppDatabase.getInstance(getApplicationContext());
        noPlantsOnList();
        if (this.getSharedPreferences("com.final_project.plantidentifier", Context.MODE_PRIVATE).getBoolean("tutorial", true))
            Toast.makeText(this, R.string.delete_saved_flower, Toast.LENGTH_LONG).show();
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

    /*
    every time we delete an plant from my plants, we remove the notification it had
     */
    private void removeNotification(int id){
        AlarmManager am =  (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        intent.putExtra("notification", builder.build());
        intent.putExtra("id", id);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 3000);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        am.cancel(pi);
        Log.d(TAG, "removed item with notifications");
    }

    private void noPlantsOnList(){
        if (mDb.plantDao().loadAllTasks().isEmpty()){
            mTvNoSaved.setVisibility(View.VISIBLE);
        }
    }

    private void retrievePlants(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<PlantEntry> tasks = mDb.plantDao().loadAllTasks();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFlowerAdapter.setTasks(tasks);
                    }
                });
            }
        });
    }

    /*
    on click for items in the recycle list - open my info page for flower
     */
    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(MyPlantsActivity.this, MyFlowerInfo.class);
        intent.putExtra("id", itemId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        noPlantsOnList();
        retrievePlants();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}