package com.madhabdhakal.dropbox.examples.android;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.dropbox.core.examples.android.R;
import com.madhabdhakal.dropbox.examples.android.model.JobsModel;
import com.madhabdhakal.dropbox.examples.android.sampledata.DbContext;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.FullAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Activity that shows information about the currently logged in user
 */
public class UserActivity extends DropboxActivity {
    private static final String TAG = UserActivity.class.getName();
    private EditText editBarcodeText;
    Button btnOk, btnUploadPhoto;
    private List<String> myList;
    private String ACCESS_TOKEN, partFilename;
    Uri fileUri;
    public Bitmap bitmap;
    Uri photoURI;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int IMAGE_REQUEST_CODE = 123;
    boolean isAllFieldsChecked = false;
    LinearLayout llBarcodeLayout;
    private static final int PICKFILE_REQUEST_CODE = 10;
    public ImageView cameraImageView;
    private String mPath;
    public final static String EXTRA_PATH = "UserActivity_Path";
    private static final String DBACCESS_TOKEN = "<ACCESS TOKEN>";
    private static final int CONTENT_REQUEST = 1337;
    private File output = null;
    String currentPhotoPath;
    SharedPreferences shpf;
    public static final String MyPREFERENCES = "MyPrefs";
    String scanContent, scanbarcodecontent;
    FloatingActionButton fab;
    int increase = 0;
    File mFiles, files;
    Bitmap bitmap001;
    public Uri picUri;
    private String mCurrentPhotoPath;
    Bitmap result_two;
    String _Location;
    Location location,  NewLocation;;
    NetworkMonitor networkMonitor;
    TimeService timeService;


    public static Intent getIntent(Context context, String path) {
        Intent filesIntent = new Intent(context, UserActivity.class);
        filesIntent.putExtra(UserActivity.EXTRA_PATH, path);
        return filesIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        networkMonitor = new NetworkMonitor();
        registerReceiver();
        timeService = new TimeService();
        shpf = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit().remove("count").commit();
        String path = getIntent().getStringExtra(EXTRA_PATH);
        mPath = path == null ? "" : path;
        editBarcodeText = findViewById(R.id.txtBarcode);
        myList = new ArrayList<String>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        Button loginButton = (Button) findViewById(R.id.btnLogin);
        btnOk = (Button) findViewById(R.id.btnOkay);
        llBarcodeLayout = findViewById(R.id.BarcodeLayout);
        cameraImageView = (ImageView) findViewById(R.id.imageView);
        btnUploadPhoto = (Button) findViewById(R.id.btnUploadPhoto);
        performWithPermissions(FileAction.LOCATION);
        performWithPermissions(FileAction.CORELOCATION);
        editBarcodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (editBarcodeText.length() >= 6) {
                            btnUploadPhoto.setVisibility(View.VISIBLE);
                            btnOk.setVisibility(View.VISIBLE);

                        } else {
                            btnUploadPhoto.setVisibility(View.GONE);
                            btnOk.setVisibility(View.GONE);
                        }

                    }
                }, 1000);

            }
        });

        // Dropbox Login on Login Button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DropboxActivity.startOAuth2Authentication(UserActivity.this, getString(R.string.app_key), Arrays.asList("account_info.read", "files.content.write"));
            }
        });

        //set the token to authorize the person
        ACCESS_TOKEN = retrieveAccessToken();

        // on click event of button scan
        Button scanButton = (Button) findViewById(R.id.btnScan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(UserActivity.this);
                intentIntegrator.initiateScan();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnUploadPhoto.setVisibility(View.VISIBLE);
                        btnOk.setVisibility(View.VISIBLE);
                    }
                }, 1000);
            }
        });

        // on click event of button take photo
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFieldsChecked = CheckBarcodeField();
                String FinalBarcode = checkBarcodes(String.valueOf(editBarcodeText.getText()));
                Log.e("Final Barcode", "Final Code after the input is: " + FinalBarcode);
                SharedPreferences.Editor editor = shpf.edit();
                editor.putString("BARCODE", FinalBarcode);
                editor.commit();
                editBarcodeText.setText(FinalBarcode);
                if (isAllFieldsChecked) {
                    performWithPermissions(FileAction.CAPTUREImage);
                }
            }
        });

        // on click event of button upload image
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //performWithPermissions(FileAction.UPLOAD);
                uploadImagetoDropbox(UserActivity.this);
            }
        });

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                NewLocation = location;
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                return true;
            case R.id.logout:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasToken()) {
            findViewById(R.id.btnLogin).setVisibility(View.GONE);
            findViewById(R.id.email_text).setVisibility(View.VISIBLE);
            findViewById(R.id.name_text).setVisibility(View.VISIBLE);
            findViewById(R.id.type_text).setVisibility(View.VISIBLE);
            findViewById(R.id.btnFiles).setEnabled(false);
            findViewById(R.id.btnScan).setVisibility(View.VISIBLE);
            findViewById(R.id.BarcodeLayout).setVisibility(View.VISIBLE);
            startService(new Intent(this, TimeService.class));


        } else {
            findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
            findViewById(R.id.email_text).setVisibility(View.GONE);
            findViewById(R.id.name_text).setVisibility(View.GONE);
            findViewById(R.id.type_text).setVisibility(View.GONE);
            findViewById(R.id.btnFiles).setEnabled(false);
            findViewById(R.id.btnScan).setVisibility(View.GONE);
            findViewById(R.id.BarcodeLayout).setVisibility(View.GONE);
        }
        //reCreateActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                //remove unwanted characters.
                String strCleanBarcode = removeSlash(intentResult.getContents());
                SharedPreferences.Editor editor = shpf.edit();
                editor.putString("BARCODE", strCleanBarcode);
                editor.commit();

                Toast.makeText(UserActivity.this, "" + strCleanBarcode, Toast.LENGTH_LONG).show();

                editBarcodeText.setText(strCleanBarcode);
                //findViewById(R.id.btnScan).setVisibility(View.GONE);
            }
        }

        if (requestCode == IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mFiles = new File(currentPhotoPath);
                bitmap001 = BitmapFactory.decodeFile(mFiles.toString());
                String gpsData = getMetaAddress();
                String jobId = shpf.getString("BARCODE", null);

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);

                TimeZone tz = TimeZone.getTimeZone("GMT+10");
                Calendar calendar = Calendar.getInstance(tz);
                String time = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                        String.format("%02d", calendar.get(Calendar.MINUTE)) + ":" +
                        String.format("%02d", calendar.get(Calendar.SECOND));
                String timeStampForWatermark = formattedDate + " : " + time;

                Bitmap result = drawJobNumber(getApplicationContext(), bitmap001, jobId + ", " + timeStampForWatermark, 20, 80, 200, 50, false);

                if (new CheckConnection(getApplicationContext()).isConnected()) {
                    fun_getmetadata();
                    result_two = drawLocationDetails(getApplicationContext(), result, gpsData + " ", 20, 160, 200, 50, false);
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        result_two = drawLatLong(getApplicationContext(), result_two, "Lat: " + latitude + ", Long: " + longitude, 20, 240, 200, 50, false);
                    }
                } else {
                    getLastKnownLocation();
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        result_two = drawLocationDetails(getApplicationContext(), result, " ", 20, 200, 200, 50, false);
                        result_two = drawLatLong(getApplicationContext(), result_two, "Latitude : " + latitude + " , Longitude : " + longitude, 20, 160, 200, 50, false);
                    }
                }
                files = storeImage(result_two);
                myList.add(files.getAbsolutePath());

                //Deleting the photos from phone memory
                File mFile2 = new File(currentPhotoPath);
                if (mFile2.exists()) {
                    mFile2.delete();
                }

                if (files != null) {
                    if (new CheckConnection(getApplicationContext()).isConnected()) {
                        String str_path = files.getAbsolutePath();
                        JobsModel jobsModel;
                        jobsModel = new JobsModel(-1, jobId, "Truck 1", str_path, gpsData, false, formattedDate);

                        DbContext dbContext = new DbContext(UserActivity.this);
                        boolean success = dbContext.addOne(jobsModel);
                        Toast.makeText(UserActivity.this, "Image Added = " + success, Toast.LENGTH_SHORT).show();

                    } else {
                        SharedPreferences.Editor editor = shpf.edit();
                        String str_path = files.getAbsolutePath();
                        int str = shpf.getInt("Count", 0);
                        editor.putInt("count", str + 1);
                        editor.putString("Key" + (str + 1), str_path);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Your images will be  automatically uploaded to dropbox when connect to internet", Toast.LENGTH_SHORT).show();
                        JobsModel jobsModel;
                        jobId = shpf.getString("BARCODE", null);
                        jobsModel = new JobsModel(-1, jobId, "Truck 1", str_path, gpsData, false, formattedDate);
                        DbContext dbContext = new DbContext(UserActivity.this);
                        boolean success = dbContext.addOne(jobsModel);
                        Toast.makeText(UserActivity.this, "Success = " + success, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                uploadFile(data.getData().toString());
            }
        }
    }


    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                ((TextView) findViewById(R.id.email_text)).setText(result.getEmail());
                ((TextView) findViewById(R.id.name_text)).setText(result.getName().getDisplayName());
                ((TextView) findViewById(R.id.type_text)).setText(result.getAccountType().name());
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get account details.", e);
            }
        }).execute();
    }

    @Override
    public void onRequestPermissionsResult(int actionCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        UserActivity.FileAction action = UserActivity.FileAction.fromCode(actionCode);
        boolean granted = true;
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Log.w(TAG, "User denied " + permissions[i] +
                        " permission to perform file action: " + action);
                granted = false;
                break;
            }
        }
        if (granted) {
            performAction(action);
        } else {
            switch (action) {
                case UPLOAD:
                case DOWNLOAD:
                    Toast.makeText(this,
                            "Can't upload file: read access denied. " +
                                    "Please grant storage permissions to use this functionality.",
                            Toast.LENGTH_LONG)
                            .show();
                    break;
                case CAPTUREImage:
                    Toast.makeText(this,
                            "Can't open Camera: Camera access denied. " +
                                    "Please grant storage permissions to use this functionality.",
                            Toast.LENGTH_LONG)
                            .show();
                    break;
                case INTERNET:
                    new CheckConnection(getApplicationContext()).showConnectionErrorDialog();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(networkMonitor);
        super.onDestroy();
    }

    /**
     * These are the helper method section, where required methods and function are written.
     */

    // method to check the barcode text field has a value or not: returns true or false
    private boolean CheckBarcodeField() {
        if (editBarcodeText.length() == 0) {
            editBarcodeText.setError("This field is required");
            return false;
        }
        if (editBarcodeText.length() < 6) {
            editBarcodeText.setError("Barcode number should be more than 6 characters");
            return false;
        }
        return true;
    }

    // method to remove slash from the barcode string
    private String removeSlash(String barcode) {
        String newBarcode;
        if (barcode.contains("/")) {
            newBarcode = barcode.replace("/", "---");
        } else {
            newBarcode = barcode;
        }
        return newBarcode;
    }

    // method to get the Access - token
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

    // method to upload the file on drive through file picker
    public void uploadFile(String fileUri) {
//        final ProgressDialog dialog = new ProgressDialog(UserActivity.this);
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setCancelable(false);
//        dialog.setMessage("Uploading...");
//        dialog.show();

        Log.i("Uploading to Dropbox", "Started uploading");
        new UploadFileTask(this, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {

                Log.i("On Complete Result: ", result.toString());
                //dialog.dismiss();

                String message = result.getName() + " size " + result.getSize() + " modified " +
                        DateFormat.getDateTimeInstance().format(result.getClientModified());
                Toast.makeText(UserActivity.this, message, Toast.LENGTH_SHORT)
                        .show();
                // Reload the folder
                //loadData();
            }

            @Override
            public void onError(Exception e) {
                //dialog.dismiss();

                Log.e("Uploading to Dropbox", "Failed to upload file.", e);
                Toast.makeText(UserActivity.this,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(fileUri, mPath);
    }

    // Lists of device permission and it's initialization
    private enum FileAction {
        DOWNLOAD(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        UPLOAD(Manifest.permission.READ_EXTERNAL_STORAGE),
        CAPTUREImage(Manifest.permission.CAMERA),
        LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
        CORELOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
        INTERNET(Manifest.permission.INTERNET);

        private static final UserActivity.FileAction[] values = values();

        private final String[] permissions;

        FileAction(String... permissions) {
            this.permissions = permissions;
        }

        public int getCode() {
            return ordinal();
        }

        public String[] getPermissions() {
            return permissions;
        }

        public static UserActivity.FileAction fromCode(int code) {
            if (code < 0 || code >= values.length) {
                throw new IllegalArgumentException("Invalid FileAction code: " + code);
            }
            return values[code];
        }
    }

    // method to check the permission and act accordingly
    private void performWithPermissions(final FileAction action) {
        if (hasPermissionsForAction(action)) {
            performAction(action);
            return;
        }
        if (shouldDisplayRationaleForAction(action)) {
            switch (action) {
                case CAPTUREImage:
                case UPLOAD:
                case DOWNLOAD:
                    new AlertDialog.Builder(this)
                            .setMessage("This app requires camera access to take a pictures.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissionsForAction(action);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                    break;
                case LOCATION:
                    new AlertDialog.Builder(this)
                            .setMessage("This app requires location access.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissionsForAction(action);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                    break;
                case INTERNET:
                    new AlertDialog.Builder(this)
                            .setMessage("This app requires Internet access.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissionsForAction(action);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                    break;
            }

        } else {
            requestPermissionsForAction(action);
        }
    }

    // function to check the permission whether it's granted or not, returns true or false
    private boolean hasPermissionsForAction(UserActivity.FileAction action) {
        for (String permission : action.getPermissions()) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // method to upload, capture and select photo based on the action
    private void performAction(UserActivity.FileAction action) {
        switch (action) {
            case CAPTUREImage:
                try {
                    dispatchTakePictureIntent();
                } catch (ActivityNotFoundException e) {
                    //display error state to the user.
                }
                break;
            case UPLOAD:
                launchFilePicker();
                break;
            default:
                Log.e(TAG, "Can't perform unhandled file action: " + action);
        }
    }

    // function to check permission if not granted, return true or false
    private boolean shouldDisplayRationaleForAction(UserActivity.FileAction action) {
        for (String permission : action.getPermissions()) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    // method to ask permission if not granted
    private void requestPermissionsForAction(UserActivity.FileAction action) {
        ActivityCompat.requestPermissions(
                this,
                action.getPermissions(),
                action.getCode()
        );
    }


    // method to open a file picker
    private void launchFilePicker() {
        // Launch intent to pick file for upload
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    // method to open a camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.UserActivity",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE);
            }
        }
    }

    // Function to store a image in string as bitmap
    private File storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100/*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(pictureFile);
            //write the bytes in file
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e("", "Error accessing file: " + e.getMessage());
        }

        return pictureFile;
    }

    // function to create a folder based on the job Id
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        increase++;
        String mImageName = shpf.getString("BARCODE", null) + "_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    // Function to get the location details
    public String getMetaAddress() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Location locations = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    _Location = listAddresses.get(0).getAddressLine(0);//
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return _Location;
    }

    // Function to upload the image to the Dropbox
    private void uploadImagetoDropbox(Context context) {
        DbContext dbc = new DbContext(context);
        List<JobsModel> jobsm = dbc.getAllJobs();
        File imagesFolders;
        int jobCounts = jobsm.size();
        Toast.makeText(context, "Uploading...", Toast.LENGTH_SHORT).show();
        if (jobCounts > 0) {
            for (int i = 0; i < jobCounts; i++) {
                JobsModel newJobs = jobsm.get(i);
                String imgUrlPaths = newJobs.getImageUrl();
                imagesFolders = new File(imgUrlPaths);
                int a = i + 1;
                File finalImagesFolders = imagesFolders;
                new ImageUploadTask(DropboxClientFactory.getClient(), finalImagesFolders, context, a) {
                    @Override
                    public String getstring(String str) {
                        Log.d("Result Output", "The output result is: " + str);
                        //Delete the image after uploading
                        dbc.deleteJobs(newJobs);
                        return null;
                    }
                }.execute();
            }
        } else {
            Toast.makeText(context, "There is no images, Please click image first!.", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to create image path based on the bitmap image
    private File createImageFile() throws IOException {
        String barcodeText = editBarcodeText.getText().toString().replace("---", "");
        Log.i("Barcode Text", barcodeText);

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + barcodeText + "_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        preferences.edit().putString("imgPath", null).commit();
        return image;
    }

    // function to register the network broadcast receiver
    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkMonitor, intentFilter);
    }

    //Function to write a Job Number on Image
    public static Bitmap drawJobNumber(Context context, Bitmap src, String watermark, int x, int y, int alpha, int size, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        int paintColor = ContextCompat.getColor(context, R.color.colorTextImage);

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(watermark, x, y, paint);
        return result;

    }

    //Function to write a Job Location Details (Maps) on Image
    public static Bitmap drawLocationDetails(Context context, Bitmap src, String watermark, int x, int y, int alpha, int size, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        int paintColor = ContextCompat.getColor(context, R.color.colorTextImage);

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(watermark, x, y, paint);
        return result;
    }

    //Function to write a GPS Coordinates on Image
    public static Bitmap drawLatLong(Context context, Bitmap src, String mLatitude, int x, int y, int alpha, int size, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        int paintColor = ContextCompat.getColor(context, R.color.colorTextImage);

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(mLatitude, x, y, paint);
        return result;
    }

    //Function to get getMeta Address Details
    public void fun_getmetadata() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    //Function to get the last location if no internet available
    private Location getLastKnownLocation() {
        Location l = null;
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        location = bestLocation;
        return bestLocation;
    }


    // function to recreate an Activity if it's stop or pause.
    private void reCreateActivity() {
        //Delaying activity recreate by 1 Milisecond. If the recreate is not delayed and is done
        //immediately in onResurme() we well get RuntimeException: Performing pause of activity that is not resumed.

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        }, 1);
    }

    private String checkBarcodes(String barcode) {
        if (!barcode.contains("---")) {
            Log.e("Barcode Test", "Barcode Does not contains Data");
            barcode = barcode + "---01";
        }

        return barcode;
    }


}
