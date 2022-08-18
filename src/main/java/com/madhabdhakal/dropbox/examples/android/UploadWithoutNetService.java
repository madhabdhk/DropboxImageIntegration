package com.madhabdhakal.dropbox.examples.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

public class UploadWithoutNetService extends Service {
    SharedPreferences sh;
    List<String> arrlist;
    SharedPreferences preferences;
    String ACCESS_TOKEN;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        sh = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        ACCESS_TOKEN = retrieveAccessToken();
        if (ACCESS_TOKEN == null) {
            Toast.makeText(getApplicationContext(), "AccessToken null", Toast.LENGTH_SHORT).show();

        } else {
            if (new CheckConnection(getApplicationContext()).isConnected()) {
                fun_uploadbtntodropbox();
            }
        }
        return Service.START_NOT_STICKY;

    }


    private void fun_uploadbtntodropbox() {

        final int count = sh.getInt("count", 0);
        File imagesFolder;
        for (int i = 1; i <= count; i++) {
            String getpath = sh.getString("Key" + i, null);
            imagesFolder = new File(getpath);
            int a = i + 1;

            new UploadTaskWithoutIntenet(DropboxClientFactory.getClient(), imagesFolder, UploadWithoutNetService.this, a) {
                @Override
                public String getstring(String str) {
                    Toast.makeText(UploadWithoutNetService.this, str, Toast.LENGTH_LONG).show();
                    if (Integer.parseInt(str) == count) {
                        SharedPreferences.Editor editor = sh.edit();
                        editor.putInt("count", 0);
                        editor.commit();
                        // Update the GridView
                    }
                    return null;
                }
            }.execute();
        }
        stopSelf();

    }

    private String retrieveAccessToken() {
        //check if ACCESS_TOKEN is previously stored on previous app launches
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        preferences.edit().putString("token", null).commit();
        //get the token from server.
        String accessToken = preferences.getString("token", "");

        if (accessToken == null) {
            Log.d("AccessToken Status", "No token found");
            return null;
        } else {
            //accessToken already exists
            Log.d("AccessToken Status", "Token exists");
            return accessToken;
        }
    }
}
