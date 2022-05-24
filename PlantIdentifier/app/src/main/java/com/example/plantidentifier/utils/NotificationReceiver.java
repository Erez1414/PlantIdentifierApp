package com.example.plantidentifier.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.plantidentifier.dataUtils.MyFlowerInfo.NOTIFICATION_CHANNEL_ID;


public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE );
        Notification notification = intent.getParcelableExtra("notification");
        int id = intent.getIntExtra("id", 0);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(id , notification);
        Log.d(TAG, "notified");
//        }
    }

}
