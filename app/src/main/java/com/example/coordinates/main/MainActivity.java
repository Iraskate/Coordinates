package com.example.coordinates.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.example.coordinates.R;
import com.example.coordinates.appusage.activities.AppUsageActivity;
import com.example.coordinates.location.activities.LocationActivity;

import static android.app.AppOpsManager.MODE_ALLOWED;

public class MainActivity extends AppCompatActivity {

    private Button buttonOpenLocationActivity, buttonOpenAppUsageActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOpenLocationActivity = findViewById(R.id.button_open_location_activity);
        buttonOpenAppUsageActivity = findViewById(R.id.button_open_appUsage_activity);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestLocationPermission();

        if(!checkUsageStatsPermission())
            requestAppUsagePermission();

        openLocationActivity();
        openAppUsageActivity();

    }

    private boolean checkUsageStatsPermission() {

        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), getPackageName());

        return mode == MODE_ALLOWED;

    }

    private void requestLocationPermission() {

        new AlertDialog.Builder(this)
                .setTitle("Location Tracking Permission")
                .setMessage("Location tracking is needed to find which places you visit the most.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, 1);

                    }
                })
                .create()
                .show();

    }

    private void requestAppUsagePermission() {

        new AlertDialog.Builder(this)
                .setTitle("App Usage Permission")
                .setMessage("App usage is needed to find which apps you use the most.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

                    }
                })
                .create()
                .show();

    }

    private void openLocationActivity() {

        buttonOpenLocationActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent activity = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(activity);

            }
        });

    }

    private void openAppUsageActivity() {

        buttonOpenAppUsageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent activity = new Intent(MainActivity.this, AppUsageActivity.class);
                startActivity(activity);

            }
        });

    }

}
