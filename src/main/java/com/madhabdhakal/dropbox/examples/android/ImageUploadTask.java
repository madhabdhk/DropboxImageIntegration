package com.madhabdhakal.dropbox.examples.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ImageUploadTask extends AsyncTask implements CallBack {
    public static final String MyPREFERENCES = "MyPrefs";
    private DbxClientV2 dbxClient;
    private File file;
    private Context mcontext;
    int a_val;
    private ProgressDialog mProgressDialog;
    SharedPreferences sh;

    ImageUploadTask(DbxClientV2 dbxClient, File file, Context context, int a) {
        this.dbxClient = dbxClient;
        this.file = file;
        this.mcontext = context;
        this.a_val = a;
//        mProgressDialog = new ProgressDialog(mcontext);
//        mProgressDialog.setMessage("Uploading file");
//        // mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
        sh = mcontext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            // Upload to Dropbox
            InputStream inputStream = new FileInputStream(file);
            dbxClient.files().uploadBuilder("/" + file.getName().replace("---", "/")) //Path in the user's Dropbox to save the file.
                    .withMode(WriteMode.OVERWRITE) //always overwrite existing file
                    .uploadAndFinish(inputStream);
            Log.d("Upload Status>>>>>>>>>", "Success");
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //mProgressDialog.cancel();
        Toast.makeText(mcontext, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
        getstring(String.valueOf(a_val));

    }
}
