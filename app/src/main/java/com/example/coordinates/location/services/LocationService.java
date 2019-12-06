package com.example.coordinates.location.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.coordinates.location.misc.DatabaseHelper;
import com.example.coordinates.location.activities.LocationActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.coordinates.main.App.CHANNEL_1_ID;

public class LocationService extends Service {

    public final static int UPDATE_TIME = 1000 * 60 * 5; //Time between location updates in milliseconds
    public final static double UPDATE_DISTANCE = 0.050; //Distance between location updates in kilometers

    private boolean insertionCompleted;

    private PowerManager.WakeLock mWakeLock;

    private String latitude, longitude, speed, altitude, dateTime;
    private String lastLatitude, lastLongitude;
    private String secondToLastLatitude, secondToLastLongitude;

    private DatabaseHelper myDb;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();

        myDb = new DatabaseHelper(this);
        fusedLocationProviderClient = new FusedLocationProviderClient(this);

        initWakeLock(); //Creates a wakelock
        initServiceNotification(); //Creates a notification if SDK version is > 26
        initLocationCallback();

    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Location Service", "Started");

        mWakeLock.acquire();

        getLocation();

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.d("Location Service", "Destroyed");

        if(fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        mWakeLock.release();

    }

    private void initWakeLock() {

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);

        if(pm != null)
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "myApp:myWakeLock");

    }

    private void initServiceNotification() {

        Intent notificationIntent = new Intent(this, LocationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setContentTitle("Location Service")
                .setContentText("Coordinates Location Running")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

    }

    private void getLocation() {

        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(UPDATE_TIME);
        locationRequest.setInterval(UPDATE_TIME + 2000);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());

    }

    private void initLocationCallback() {

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                insertionCompleted = false;

                getLocationInformation(locationResult); //Gets coordinates
                getLastLocation();
                getSecondToLastLocation();
                saveLocationToDatabase();

            }
        };

    }

    private void getLocationInformation(LocationResult locationResult) {

        this.latitude = String.valueOf(locationResult.getLastLocation().getLatitude());
        this.longitude = String.valueOf(locationResult.getLastLocation().getLongitude());
        this.speed = String.valueOf(locationResult.getLastLocation().getSpeed());
        this.altitude = String.valueOf(locationResult.getLastLocation().getAltitude());

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yyyy, HH:mm:ss", Locale.getDefault());

        this.dateTime = sdf.format(new Date());

    }

    private boolean insertCoordinates(String latitude, String longitude, String speed, String altitude, String dateTime) {

        boolean inserted = myDb.insertData(DatabaseHelper.TABLE_NAME_1, latitude, longitude, speed, altitude, dateTime, null, 0, null); //Insert coordinates

        //Check if insertion is completed
        if(inserted)
            Toast.makeText(LocationService.this, "Coordinates Inserted", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(LocationService.this, "Coordinates Not Inserted", Toast.LENGTH_SHORT).show();

        return inserted;

    }

    private void getLastLocation() {

        Cursor res = myDb.getLastRecord(DatabaseHelper.TABLE_NAME_1);

        if(res.getCount() == 0) {
            return;
        }

        while(res.moveToNext()) {

            this.lastLatitude = res.getString(res.getColumnIndex(DatabaseHelper.COL_2_1));
            this.lastLongitude = res.getString(res.getColumnIndex(DatabaseHelper.COL_3_1));
        }

    }

    private void getSecondToLastLocation() {

        Cursor res = myDb.getSecondToLastRecord(DatabaseHelper.TABLE_NAME_1);

        if(res.getCount() == 0) {
            return;
        }

        while(res.moveToNext()) {

            this.secondToLastLatitude = res.getString(res.getColumnIndex(DatabaseHelper.COL_2_1));
            this.secondToLastLongitude = res.getString(res.getColumnIndex(DatabaseHelper.COL_3_1));
        }

    }

    private void saveLocationToDatabase() {

        if(lastLatitude != null && lastLongitude != null) {

            //If the distance between last location and current location is bigger than UPDATE_DISTANCE kilometers then do the following
            if (distance(Double.parseDouble(lastLatitude), Double.parseDouble(lastLongitude), Double.parseDouble(latitude), Double.parseDouble(longitude)) > UPDATE_DISTANCE) {

                //Check to see if the current location is the same as the second to last location
                if (distance(Double.parseDouble(secondToLastLatitude), Double.parseDouble(secondToLastLongitude), Double.parseDouble(latitude), Double.parseDouble(longitude)) < UPDATE_DISTANCE) {

                    int deletedRows = myDb.deleteLastRow(DatabaseHelper.TABLE_NAME_1);

                    if (deletedRows > 0)
                        Log.d("Last Row", "Deleted");

                }

            }

        }

        insertionCompleted = insertCoordinates(latitude, longitude, speed, altitude, dateTime); //Sends coordinates to SQLite database

        if (insertionCompleted) { //Saves database to SD Card, if there is one available

            myDb.saveDatabaseToSDCard();

        }

        stopSelf();

    }

    public static double distance(double lastLat, double lastLong, double currentLat, double currentLong) {

        double earthRadius = 6371; // in kilometers, change to 3958.75 for miles output

        double dLat = Math.toRadians(currentLat-lastLat);
        double dLng = Math.toRadians(currentLong-lastLong);

        double sinLat = Math.sin(dLat / 2);
        double sinLng = Math.sin(dLng / 2);

        double a = Math.pow(sinLat, 2) + Math.pow(sinLng, 2)
                * Math.cos(Math.toRadians(lastLat)) * Math.cos(Math.toRadians(currentLat));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadius * c; // output distance, in KILOMETERS
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}