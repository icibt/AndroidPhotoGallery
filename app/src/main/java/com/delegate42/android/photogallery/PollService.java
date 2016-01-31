package com.delegate42.android.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by Ici on 26.1.2016.
 */
public class PollService extends IntentService {
    private static final String TAG = "PoolService";
    private static final int POLL_INTERVAL  = 1000 * 60; //60 secconds
    private static final int JOB_ID = 1;

    public static Intent newIntent(Context c) {
        return new Intent(c,PollService.class);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FlickerNewPhotoNotificationProvider provider = new FlickerNewPhotoNotificationProvider(this);
        provider.processWork();
    }


    public static void setServiceAlarm(Context context,boolean isOn) {
        if(Build.VERSION.SDK_INT >= 21){
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if(isOn) {
                JobInfo jobInfo = new JobInfo.Builder(JOB_ID,new ComponentName(context,PollJobService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                        .setPeriodic(1000 * 60 * 1)
                        //.setPersisted(true)
                        .build();
                scheduler.schedule(jobInfo);
            } else
            {
                scheduler.cancel(JOB_ID);
            }
        } else {
            Intent i = PollService.newIntent(context);
            PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if(isOn) {
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),POLL_INTERVAL,pi);
            } else {
                alarmManager.cancel(pi);
                pi.cancel();
            }
        }
        QueryPreferences.setAlarmOn(context,isOn);
    }

    public static boolean isServiceAlarmOn(Context context) {
        if(Build.VERSION.SDK_INT >= 21){
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            for(JobInfo jobInfo: scheduler.getAllPendingJobs()){
                if(jobInfo.getId() == JOB_ID){
                    return true;
                }
            }
            return false;
        }

        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
