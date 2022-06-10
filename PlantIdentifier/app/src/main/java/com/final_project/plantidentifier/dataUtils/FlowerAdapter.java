package com.final_project.plantidentifier.dataUtils;

import static com.final_project.plantidentifier.dataUtils.MyFlowerInfo.default_notification_channel_id;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.final_project.plantidentifier.R;
import com.final_project.plantidentifier.data.AppDatabase;
import com.final_project.plantidentifier.data.PlantEntry;
import com.final_project.plantidentifier.utils.NotificationReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FlowerAdapter extends RecyclerView.Adapter<FlowerAdapter.FlowerAdapterViewHolder> {

    private static final String DATE_FORMAT = "dd/MM/yyy";
    final private ItemClickListener mItemClickListener;
    private List<PlantEntry> mPlantEntries;
    private Context mContext;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public FlowerAdapter(Context context, ItemClickListener listener){
        mContext = context;
        mItemClickListener = listener;
    }

    public class FlowerAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener{

        public final TextView mTvName;
        public final TextView mTvKey;
        public final TextView mTvLoc;
        public final ImageView mIvPic;
        public final Button mBtnDelete;
        public int mPosition;

        public FlowerAdapterViewHolder(View view){
            super(view);
            mTvName = (TextView) view.findViewById(R.id.tv_lst_name);
            mTvKey = (TextView) view.findViewById(R.id.tv_lst_key);
            mTvLoc = (TextView) view.findViewById(R.id.tv_lst_loc);
            mIvPic = (ImageView) view.findViewById(R.id.iv_list_flower_img);
            mBtnDelete = (Button) view.findViewById(R.id.btn_delete);
            view.setOnClickListener(this);

            mBtnDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlantEntry deleted = mPlantEntries.get(mPosition);
                    mPlantEntries.remove(mPosition);
                    notifyItemRemoved(mPosition);
                    AppDatabase mDb = AppDatabase.getInstance(mContext);
                    mDb.plantDao().deleteTask(deleted);
                    removeNotification(deleted.getId());
                }
            });
        }

        /*
       every time we delete an plant from my plants, we remove the notification it had
        */
        private void removeNotification(int id){
            AlarmManager am =  (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, NotificationReceiver.class);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, default_notification_channel_id ) ;
            intent.putExtra("notification", builder.build());
            intent.putExtra("id", id);
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 3000);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            am.cancel(pi);
        }

        @Override
        public void onClick(View v) {
            int elementId = mPlantEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }


    }

    @NonNull
    @Override
    public FlowerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.plant_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new FlowerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlowerAdapterViewHolder holder, int position) {
        PlantEntry taskEntry = mPlantEntries.get(position);
        String name = taskEntry.getName();
        String key = taskEntry.getType();
        String loc = taskEntry.getLoc();
        Bitmap pic = taskEntry.getImg();
        String updatedAt = dateFormat.format(taskEntry.getUpdatedAt());

        holder.mIvPic.setImageBitmap(Bitmap.createScaledBitmap(pic, 250, 250, true));
        holder.mTvName.setText(name);
        holder.mTvKey.setText("Type: " + key);
        holder.mTvLoc.setText("Located at: " + loc);
        holder.mPosition = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        if (mPlantEntries == null) {
            return 0;
        }
        return mPlantEntries.size();
    }

    public void setTasks(List<PlantEntry> taskEntries) {
        mPlantEntries = taskEntries;
        notifyDataSetChanged();
    }

    public List<PlantEntry> getPlants() {
        return mPlantEntries;
    }

}
