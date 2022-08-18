package com.madhabdhakal.dropbox.examples.android.sampledata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.madhabdhakal.dropbox.examples.android.model.JobsModel;

import java.util.ArrayList;
import java.util.List;

public class DbContext extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DeliveryTracking.db";
    public static final int DATABASE_VERSION = 1;

    public DbContext(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //database static methods
        String CREATEJOBSTABLE = "CREATE TABLE " + DBContract.DbInitialize.TABLE_NAME + " (" + DBContract.DbInitialize.JOBS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBContract.DbInitialize.JOBS_COlUMN_ID + " TEXT, " + DBContract.DbInitialize.JOBS_EMPLOYEEID + " TEXT, " + DBContract.DbInitialize.JOBS_IMAGEURL + " TEXT, " +
                DBContract.DbInitialize.JOBS_GPSLOCATION + " TEXT, " + DBContract.DbInitialize.JOBS_ISUPLOADED + " BOOLEAN, " + DBContract.DbInitialize.JOBS_INSERT_DATE + " TEXT)";

        sqLiteDatabase.execSQL(CREATEJOBSTABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DELETEJOBSTABLE = "DROP TABLE IF EXISTS " + DBContract.DbInitialize.TABLE_NAME;
        //This database is only a cache for online data, so its upgrade policy is
        //to simply to discard the data and start over
        sqLiteDatabase.execSQL(DELETEJOBSTABLE);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    public boolean addOne(JobsModel jobsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.DbInitialize.JOBS_COlUMN_ID, jobsModel.getJobId());
        cv.put(DBContract.DbInitialize.JOBS_EMPLOYEEID, jobsModel.getEmployeeId());
        cv.put(DBContract.DbInitialize.JOBS_IMAGEURL, jobsModel.getImageUrl());
        cv.put(DBContract.DbInitialize.JOBS_GPSLOCATION, jobsModel.getGpsLocation());
        cv.put(DBContract.DbInitialize.JOBS_ISUPLOADED, jobsModel.isUploaded());
        cv.put(DBContract.DbInitialize.JOBS_INSERT_DATE, jobsModel.getInsertDate());

        long insert = db.insert(DBContract.DbInitialize.TABLE_NAME, null, cv);
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<JobsModel> getAllJobs() {

        List<JobsModel> returnLists = new ArrayList<>();

        String queryString = "SELECT * FROM " + DBContract.DbInitialize.TABLE_NAME + " WHERE " + DBContract.DbInitialize.JOBS_ISUPLOADED + " = 0";
        //String queryString = "SELECT * FROM UploadJobs uj WHERE uj.IsUploaded  = 0";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            //loop through the cursor (result set) and create new job Object
            do {
                int id = cursor.getInt(0);
                String jobId = cursor.getString(1);
                String employeeId = cursor.getString(2);
                String imageUrl = cursor.getString(3);
                String gpsLocation = cursor.getString(4);
                boolean isUploaded = cursor.getInt(5) == 1 ? true : false;
                String insertDate = cursor.getString(6);

                JobsModel newJobsModel = new JobsModel(id, jobId, employeeId, imageUrl, gpsLocation, isUploaded, insertDate);
                returnLists.add(newJobsModel);
            }
            while (cursor.moveToNext());

        } else {
            //failure. do no tadd anything to the list.
        }
        //close both cursor and t he db when done.
        cursor.close();
        db.close();
        return returnLists;
    }

    //Delete Jobs
    public void deleteJobs(JobsModel jobsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(DBContract.DbInitialize.TABLE_NAME, DBContract.DbInitialize.JOBS_ID + " = ?",
                new String[]{String.valueOf(jobsModel.getId())});
        db.close();
    }
}
