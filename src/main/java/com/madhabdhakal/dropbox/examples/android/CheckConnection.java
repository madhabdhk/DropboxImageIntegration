package com.madhabdhakal.dropbox.examples.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class CheckConnection {
    Context context;

    public CheckConnection(Context mcontext) {
        context = mcontext;
    }

    public boolean isConnected() {

        ConnectivityManager connectivitymanager;
        NetworkInfo wifiinfo, mobileinfo;
        connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiinfo = connectivitymanager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobileinfo = connectivitymanager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifiinfo.isConnected() || mobileinfo.isConnected()) {

            return true;
        }
        return false;
    }

    public boolean isGpsOn() {
        LocationManager locationmanager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    public void showGPSErrorDialouge() {
        new AlertDialog.Builder(context).setTitle("Enable GPS!").setMessage("There is problem fetching your location. Please turn on your gps.")
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent viewIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(viewIntent);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ((Activity) context).finish();

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show()
                .setCancelable(false);
    }


    public void showConnectionErrorDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Enable Connection!")
                .setMessage(
                        "There is no internet connection. Please enable your connection.")
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                context.startActivity(new Intent(
                                        Settings.ACTION_SETTINGS));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // ((Activity) context).finish();
                                dialog.cancel();

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show()
                .setCancelable(false);
    }
}
