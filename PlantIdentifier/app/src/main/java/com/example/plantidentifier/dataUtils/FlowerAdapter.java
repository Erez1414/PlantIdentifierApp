package com.example.plantidentifier.dataUtils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantidentifier.R;
import com.example.plantidentifier.data.PlantEntry;

import java.text.SimpleDateFormat;
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

        public FlowerAdapterViewHolder(View view){
            super(view);
            mTvName = (TextView) view.findViewById(R.id.tv_lst_name);
            mTvKey = (TextView) view.findViewById(R.id.tv_lst_key);
            mTvLoc = (TextView) view.findViewById(R.id.tv_lst_loc);
            mIvPic = (ImageView) view.findViewById(R.id.iv_list_flower_img);
            view.setOnClickListener(this);
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
