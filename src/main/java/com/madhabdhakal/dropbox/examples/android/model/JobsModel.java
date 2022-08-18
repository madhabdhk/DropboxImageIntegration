package com.madhabdhakal.dropbox.examples.android.model;

public class JobsModel {
    private int Id;
    private String JobId;
    private String EmployeeId;
    private String ImageUrl;
    private String GpsLocation;
    private boolean IsUploaded;
    private String InsertDate;

    //constructor

    public JobsModel(int id, String jobId, String employeeId, String imageUrl, String gpsLocation, boolean isUploaded, String insertDate) {
        Id = id;
        JobId = jobId;
        EmployeeId = employeeId;
        ImageUrl = imageUrl;
        GpsLocation = gpsLocation;
        IsUploaded = isUploaded;
        InsertDate = insertDate;
    }

    public JobsModel() {
    }

    //toString is necessary for printing the contents of a class objects.


    @Override
    public String toString() {
        return "JobsModel{" +
                "Id=" + Id +
                ", JobId='" + JobId + '\'' +
                ", EmployeeId='" + EmployeeId + '\'' +
                ", ImageUrl='" + ImageUrl + '\'' +
                ", GpsLocation='" + GpsLocation + '\'' +
                ", IsUploaded=" + IsUploaded +
                ", InsertDate='" + InsertDate + '\'' +
                '}';
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String employeeId) {
        EmployeeId = employeeId;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getGpsLocation() {
        return GpsLocation;
    }

    public void setGpsLocation(String gpsLocation) {
        GpsLocation = gpsLocation;
    }

    public boolean isUploaded() {
        return IsUploaded;
    }

    public void setUploaded(boolean uploaded) {
        IsUploaded = uploaded;
    }

    public String getInsertDate() {
        return InsertDate;
    }

    public void setInsertDate(String insertDate) {
        InsertDate = insertDate;
    }
}
