package com.delegate42.android.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by Ici on 29.1.2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Received result: "+getResultCode());
        if(getResultCode() != Activity.RESULT_OK) {
            //a foreground activity canceled the broadcast
            return;
        }
        int requestCode = intent.getIntExtra(FlickerNewPhotoNotificationProvider.REQUEST_CODE,0);
        Notification notification = intent.getParcelableExtra(FlickerNewPhotoNotificationProvider.NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode,notification);
    }
}
