package com.example.coordinates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button buttonStartService, buttonStopService, buttonViewCoordinates, buttonDeleteCoordinates, buttonViewMap, buttonViewBranding;

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); //Initialize variables

        if(!runtime_permission())
            enable_buttons();

        viewCoordinates();
        deleteCoordinates();
        openMap();
        openBranding();

    }

    private void init() {

        myDb = new DatabaseHelper(this);

        buttonStartService = findViewById(R.id.button_start_service);
        buttonStopService = findViewById(R.id.button_stop_service);
        buttonViewCoordinates = findViewById(R.id.button_view_coordinates);
        buttonDeleteCoordinates = findViewById(R.id.button_delete_coordinates);
        buttonViewMap = findViewById(R.id.button_view_map);
        buttonViewBranding = findViewById(R.id.button_view_branding);

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

                        Cursor res = myDb.getAllData();

                        if(res.getCount() == 0) { //No records error

                            showMessage("Error", "No Coordinates Found");
                            return;

                        }

                        StringBuilder buffer = new StringBuilder();

                        while(res.moveToNext()) {

                            buffer.append(DatabaseHelper.COL_1 + ": ").append(res.getString(0)).append("\n");
                            buffer.append(DatabaseHelper.COL_2 + ": ").append(res.getString(1)).append("\n");
                            buffer.append(DatabaseHelper.COL_3 + ": ").append(res.getString(2)).append("\n\n");

                        }

                        showMessage("Coordinates", buffer.toString());

                    }
                }
        );

    }

    private void deleteCoordinates() {

        buttonDeleteCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int deletedRows = myDb.deleteData();

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

                String latitude = null, longitude = null;

                Cursor res = myDb.getLastRecord();

                while(res.moveToNext()) {

                    latitude = res.getString(res.getColumnIndex(DatabaseHelper.COL_2));
                    longitude = res.getString(res.getColumnIndex(DatabaseHelper.COL_3));
                }

                if(latitude == null || longitude == null) {

                    showMessage("Error", "No coordinates available, start the service.");

                }else {

                    Intent googleMaps = new Intent(MainActivity.this, MapActivity.class);

                    googleMaps.putExtra("latitude", latitude);
                    googleMaps.putExtra("longitude", longitude);

                    startActivity(googleMaps);

                }

            }
        });

    }

    private void openBranding() {

        buttonViewBranding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent brandingIntent = new Intent(MainActivity.this, BrandingActivity.class);
                startActivity(brandingIntent);

            }
        });

    }

    private void showMessage(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

}