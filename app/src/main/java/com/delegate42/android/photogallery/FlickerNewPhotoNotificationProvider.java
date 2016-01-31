package com.delegate42.android.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by Ici on 29.1.2016.
 */
public class FlickerNewPhotoNotificationProvider {
    private static final String TAG = "FlickerPhotoNotifProv";
    public static final String ACTION_SHOW_NOTIFICATION = "com.delegate42.android.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.delegate42.android.photogallery.PRIVATE";

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    private final Context mContext;

    public FlickerNewPhotoNotificationProvider(Context c)
    {
        mContext = c;
    }

    public void processWork() {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        String query = QueryPreferences.getStoredQuery(mContext);
        String lastResultId = QueryPreferences.getLastResultId(mContext);

        Integer pageNumber = 1;
        List<GalleryItem> items;
        if (query == null) {
            items = new FlickrFetchr().fetchRecentPhotos(pageNumber);
        } else {
            items = new FlickrFetchr().searchPhotos(pageNumber, query);
        }

        if (items.size() == 0) {
            return;
        }

        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);
            QueryPreferences.setLastResultId(mContext, resultId);

            Resources resources = mContext.getResources();
            Intent i = PhotoGalleryActivity.newIntent(mContext);
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, i, 0);

            Notification notification = new NotificationCompat.Builder(mContext)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();

            showBackgroundNotification(0, notification);
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
//            notificationManager.notify(0, notification);

//            mContext.sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION),PERM_PRIVATE);
        }
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    private void showBackgroundNotification(int requestCode,Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE,requestCode);
        i.putExtra(NOTIFICATION,notification);
        mContext.sendOrderedBroadcast(i,PERM_PRIVATE,null,null, Activity.RESULT_OK,null,null);
    }
}
