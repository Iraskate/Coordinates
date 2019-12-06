package com.example.coordinates.main;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.coordinates.appusage.services.AppUsageService;
import com.example.coordinates.location.services.LocationService;

import static android.app.AppOpsManager.MODE_ALLOWED;

public class SchedulerEventReceiver extends BroadcastReceiver {

    private static final String APP_TAG = "com.example.coordinates";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(APP_TAG, "SchedulerEventReceiver.onReceive() called");

        if(checkLocationPermission(context) && checkUsageStatsPermission(context)) {

            //Location Service
            Intent eventService1 = new Intent(context, LocationService.class);
            ContextCompat.startForegroundService(context, eventService1);

            //App Usage Service
            Intent eventService2 = new Intent(context, AppUsageService.class);
            ContextCompat.startForegroundService(context, eventService2);

        }

    }

    private boolean checkUsageStatsPermission(Context context) {

        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());

        return mode == MODE_ALLOWED;

    }

    private boolean checkLocationPermission(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else
                return true;

        }

        return true;

    }
}
