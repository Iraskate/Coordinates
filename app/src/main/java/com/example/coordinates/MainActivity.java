package com.example.coordinates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button buttonStartService, buttonStopService,
            buttonViewCoordinates, buttonDeleteCoordinates,
            buttonViewMap, buttonViewBranding,
            buttonViewFinalCoordinates, buttonDeleteFinalCoordinates,
            buttonClusterLocations;

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); //Initialize variables

        if(!runtime_permission())
            enable_buttons();

        viewCoordinates();
        viewFinalCoordinates();

        deleteCoordinates();
        deleteFinalCoordinates();

        openMap();
        openBranding();

        clusterLocations();

    }

    private void init() {

        myDb = new DatabaseHelper(this);

        buttonStartService = findViewById(R.id.button_start_service);
        buttonStopService = findViewById(R.id.button_stop_service);
        buttonViewCoordinates = findViewById(R.id.button_view_coordinates);
        buttonDeleteCoordinates = findViewById(R.id.button_delete_coordinates);
        buttonViewMap = findViewById(R.id.button_view_map);
        buttonViewBranding = findViewById(R.id.button_view_branding);
        buttonViewFinalCoordinates = findViewById(R.id.button_view_final_coordinates);
        buttonDeleteFinalCoordinates = findViewById(R.id.button_delete_final_coordinates);
        buttonClusterLocations = findViewById(R.id.button_cluster_locations);

    }

    private void enable_buttons() {

        buttonStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(getApplicationContext(), LocationService.class);

                //Checks if the SDK version is higher than 26 to act accordingly
                ContextCompat.startForegroundService(MainActivity.this, serviceIntent);

            }
        });

        buttonStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(MainActivity.this, LocationService.class);
                stopService(serviceIntent);

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 10) {

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                enable_buttons();
            else
                runtime_permission();

        }

    }

    private boolean runtime_permission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET

                }, 10);

                return true;

            }
        }

        return false;

    }

    private void viewCoordinates() {

        buttonViewCoordinates.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Cursor res = myDb.getAllData(DatabaseHelper.TABLE_NAME_1);

                        if(res.getCount() == 0) { //No records error

                            showMessage("Error", "No Coordinates Found");
                            return;

                        }

                        StringBuilder buffer = new StringBuilder();

                        while(res.moveToNext()) {

                            buffer.append(DatabaseHelper.COL_1_1 + ": ").append(res.getString(0)).append("\n");
                            buffer.append(DatabaseHelper.COL_2_1 + ": ").append(res.getString(1)).append("\n");
                            buffer.append(DatabaseHelper.COL_3_1 + ": ").append(res.getString(2)).append("\n");
                            buffer.append(DatabaseHelper.COL_4_1 + ": ").append(res.getString(3)).append("\n");
                            buffer.append(DatabaseHelper.COL_5_1 + ": ").append(res.getString(4)).append("\n");
                            buffer.append(DatabaseHelper.COL_6_1 + ": ").append(res.getString(5)).append("\n\n");

                        }

                        showMessage("Coordinates", buffer.toString());

                    }
                }
        );

    }

    private void viewFinalCoordinates() {

        buttonViewFinalCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor res = myDb.getAllData(DatabaseHelper.TABLE_NAME_2);

                if(res.getCount() == 0) { //No records error

                    showMessage("Error", "No Final Coordinates Found");
                    return;

                }

                StringBuilder buffer = new StringBuilder();

                while(res.moveToNext()) {

                    buffer.append(DatabaseHelper.COL_1_2 + ": ").append(res.getString(0)).append("\n");
                    buffer.append(DatabaseHelper.COL_2_2 + ": ").append(res.getString(1)).append("\n");
                    buffer.append(DatabaseHelper.COL_3_2 + ": ").append(res.getString(2)).append("\n");
                    buffer.append(DatabaseHelper.COL_4_2 + ": ").append(res.getString(3)).append("\n");
                    buffer.append(DatabaseHelper.COL_5_2 + ": ").append(res.getString(4)).append("\n");
                    buffer.append(DatabaseHelper.COL_6_2 + ": ").append(res.getString(5)).append("\n");
                    buffer.append(DatabaseHelper.COL_7_2 + ": ").append(res.getString(6)).append("\n\n");

                }

                showMessage("Final Coordinates", buffer.toString());

            }
        });

    }

    private void deleteCoordinates() {

        buttonDeleteCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int deletedRows = myDb.deleteData(DatabaseHelper.TABLE_NAME_1);

                if(deletedRows > 0)
                    Toast.makeText(MainActivity.this, deletedRows + " Rows Deleted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "No Rows Deleted", Toast.LENGTH_LONG).show();

            }

        });

    }

    private void deleteFinalCoordinates() {

        buttonDeleteFinalCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int deletedRows = myDb.deleteData(DatabaseHelper.TABLE_NAME_2);

                if(deletedRows > 0)
                    Toast.makeText(MainActivity.this, deletedRows + " Rows Deleted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "No Rows Deleted", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void openMap() {

        buttonViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor res = myDb.getAllData(DatabaseHelper.TABLE_NAME_2);

                if(res.getCount() > 0) {

                    if (haveNetworkConnection()) {

                        Intent googleMaps = new Intent(MainActivity.this, MapActivity.class);

                        googleMaps.putExtra("id", -1);

                        startActivity(googleMaps);

                    } else
                        showMessage("Error", "No internet connection.");

                }
                else
                    showMessage("Error", "No Final Coordinates Found");

            }

        });

    }

    private void openBranding() {

        buttonViewBranding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor res = myDb.getAllData(DatabaseHelper.TABLE_NAME_2);

                if(res.getCount() == 0)
                    showMessage("Error", "No Final Coordinates Found");
                else {

                    Intent brandingIntent = new Intent(MainActivity.this, BrandingActivity.class);
                    startActivity(brandingIntent);

                }

            }
        });

    }

    private void clusterLocations() {

        buttonClusterLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clusterAllLocations();

                showMessage("Clustering Complete", "View the final locations on 'View Final Coordinates'");

            }
        });

    }

    private void clusterAllLocations() {

        int currentID;

        String currentTempLatitude, currentTempLongitude, nextTempLatitude, nextTempLongitude;

        myDb.duplicateDatabase(DatabaseHelper.TABLE_NAME_3, DatabaseHelper.TABLE_NAME_1);

        Cursor mainTable = myDb.getAllData(DatabaseHelper.TABLE_NAME_1);

        for(int i = 0; i < mainTable.getCount(); i++) {

            mainTable.moveToPosition(i);

            int totalTimeSpent = 0;

            currentTempLatitude = mainTable.getString(1);
            currentTempLongitude = mainTable.getString(2);

            Cursor compareTable = myDb.getAllData(DatabaseHelper.TABLE_NAME_3);

            for(int j = 0; j < compareTable.getCount(); j++) {

                compareTable.moveToPosition(j);

                currentID = compareTable.getInt(0);
                nextTempLatitude = compareTable.getString(1);
                nextTempLongitude = compareTable.getString(2);

                if (LocationService.distance(Double.parseDouble(nextTempLatitude), Double.parseDouble(nextTempLongitude), Double.parseDouble(currentTempLatitude), Double.parseDouble(currentTempLongitude)) < LocationService.UPDATE_DISTANCE) {

                    if (myDb.deleteDataByRowID(DatabaseHelper.TABLE_NAME_3, currentID) > 0)
                        totalTimeSpent++;

                }

            }

            if(totalTimeSpent > 0) {

                Cursor finalTable = myDb.getAllData(DatabaseHelper.TABLE_NAME_2);

                totalTimeSpent = (totalTimeSpent * LocationService.UPDATE_TIME) / 60000;

                if(finalTable.getCount() == 0) {

                     myDb.insertData(DatabaseHelper.TABLE_NAME_2,
                            currentTempLatitude,
                            currentTempLongitude,
                            mainTable.getString(3),
                            mainTable.getString(4),
                            mainTable.getString(5),
                            String.valueOf(totalTimeSpent),
                            0,
                            null);

                }
                else {

                    int lastListViewNumber = 0;

                    Cursor finalTableLastListViewNumber = myDb.getLastRecord(DatabaseHelper.TABLE_NAME_2);

                    while(finalTableLastListViewNumber.moveToNext())
                        lastListViewNumber = finalTableLastListViewNumber.getInt(finalTableLastListViewNumber.getColumnIndex(DatabaseHelper.COL_8_2));

                    myDb.insertData(DatabaseHelper.TABLE_NAME_2,
                            currentTempLatitude,
                            currentTempLongitude,
                            mainTable.getString(3),
                            mainTable.getString(4),
                            mainTable.getString(5),
                            String.valueOf(totalTimeSpent),
                            lastListViewNumber + 1,
                            null);

                }

            }

        }

        myDb.deleteData(DatabaseHelper.TABLE_NAME_1);
        myDb.deleteData(DatabaseHelper.TABLE_NAME_3);

    }

    private void showMessage(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    private boolean haveNetworkConnection() {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}