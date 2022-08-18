package com.madhabdhakal.dropbox.examples.android;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.madhabdhakal.dropbox.examples.android.model.JobsModel;
import com.madhabdhakal.dropbox.examples.android.sampledata.DbContext;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends Service {

    public static final long NOTIFY_INTERNAL = 300000; //check every 30 minutes
    //run on another thread to avoid crash
    private Handler mHandler = new Handler();

    //timer handling
    private Timer mTimer = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        //cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }

        //schedule tasks
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERNAL);

    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (new CheckConnection(getApplicationContext()).isConnected()) {
                        Log.d("TimeMachine", "I am on it.");

                        DbContext dbcc = new DbContext(getApplicationContext());
                        List<JobsModel> jModels = dbcc.getAllJobs();
                        File imgFolders;

                        int jbCounts = jModels.size();

                        if (jbCounts > 0) {
                            for (int i = 0; i < jbCounts; i++) {
                                JobsModel newJobs = jModels.get(i);
                                String imgUrlPaths = newJobs.getImageUrl();
                                imgFolders = new File(imgUrlPaths);
                                int a = i + 1;

                                File finalMFiles = imgFolders;

                                new DropboxUploadTask(DropboxClientFactory.getClient(), imgFolders, getApplicationContext(), a) {
                                    @Override
                                    public String getstring(String str) {
                                        Log.d("Job Scheduler", "The output result is: " + str);
                                        //Delete the image after uploading
                                        dbcc.deleteJobs(newJobs);
                                        return null;
                                    }
                                }.execute();
                            }
                        }
                        Log.d("Job Scheduler", "Job Completed.");
                    }
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }
    }
}

