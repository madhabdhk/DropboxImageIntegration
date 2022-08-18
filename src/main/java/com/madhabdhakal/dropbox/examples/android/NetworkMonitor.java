package com.madhabdhakal.dropbox.examples.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.madhabdhakal.dropbox.examples.android.model.JobsModel;
import com.madhabdhakal.dropbox.examples.android.sampledata.DbContext;

import java.io.File;
import java.util.List;

public class NetworkMonitor extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (checkNetworkConnection(context)) {

            uploadImagetoDropbox(context);
            //Log.e("Madhab", "Image Uploaded");
        } else {
            Log.e("Madhab", "Netowrk not found");
        }

    }

    private boolean checkNetworkConnection(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void uploadImagetoDropbox(Context context) {
        DbContext db = new DbContext(context);
        List<JobsModel> jobs = db.getAllJobs();
        File imagesFolder;
        int jobCount = jobs.size();
        if (jobCount > 0) {
            for (int i = 0; i < jobCount; i++) {
                JobsModel newJobs = jobs.get(i);
                String imgUrlPath = newJobs.getImageUrl();
                imagesFolder = new File(imgUrlPath);

                File f = new File(imgUrlPath);
                f = new File(f.getAbsolutePath());
                String parentPath = imagesFolder.getParent();
                String newImagePath = parentPath + "/Pictures";

                int a = i + 1;

                File mFinalImageFolder = imagesFolder;
                new DropboxUploadTask(DropboxClientFactory.getClient(), imagesFolder, context, a) {
                    @Override
                    public String getstring(String str) {
                        Log.e("Madhab", str);
                        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
                        if (Integer.parseInt(str) == jobs.size()) {
                            File mFiles = new File(mFinalImageFolder.getPath());

                            //Delete the image after uploading
                            db.deleteJobs(newJobs);

                            if (mFiles.exists()) {
                                mFiles.delete();
                                File dir = new File(Environment.getExternalStorageState() + "Pictures");
                                if (dir.isDirectory()) {
                                    String[] children = dir.list();
                                    for (int i = 0; i < children.length; i++) {
                                        new File(dir, children[i]).delete();
                                    }
                                }

                            }
                        }

                        return null;
                    }
                }.execute();


            }
            Log.e("Madhab", "Job does not exists");

        }
    }


}
