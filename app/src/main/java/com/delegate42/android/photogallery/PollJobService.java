package com.delegate42.android.photogallery;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by Ici on 28.1.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {
    private static final String TAG = "PollJobService";
    private PollTask mCurrentTask;

    @Override
    public boolean onStartJob(JobParameters params) {
        mCurrentTask = new PollTask(this);
        mCurrentTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private class PollTask extends AsyncTask<JobParameters,Void,Void> {
        Context mContext;
        public PollTask(Context c){
            mContext = c;
        }
        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters jobParameters = params[0];
            FlickerNewPhotoNotificationProvider provider = new FlickerNewPhotoNotificationProvider(mContext);
            provider.processWork();
            jobFinished(jobParameters,false);
            return null;
        }
    }
}
