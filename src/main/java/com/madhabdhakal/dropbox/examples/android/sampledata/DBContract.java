package com.madhabdhakal.dropbox.examples.android.sampledata;

import android.provider.BaseColumns;

public class DBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBContract() {
    }

    /* Inner class that defines the table contents */
    public static class DbInitialize implements BaseColumns {
        public static final String TABLE_NAME = "UploadJobs";
        public static final String JOBS_ID = "Id";
        public static final String JOBS_COlUMN_ID = "JobId";
        public static final String JOBS_EMPLOYEEID = "EmployeeId";
        public static final String JOBS_IMAGEURL = "ImageUrl";
        public static final String JOBS_GPSLOCATION = "GpsLocation";
        public static final String JOBS_ISUPLOADED = "IsUploaded";
        public static final String JOBS_INSERT_DATE = "InsertDate";
    }
}
