package com.example.coordinates.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.coordinates.location.services.LocationService;

public class SchedulerSetupReceiver extends BroadcastReceiver {

    private static final String APP_TAG = "com.example.coordinates";
    private static final int EXEC_INTERVAL = 10000;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(APP_TAG, "SchedulerSetupReceiver.onReceive() called");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, SchedulerEventReceiver.class);

        PendingIntent intentExecuted = PendingIntent.getBroadcast(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), EXEC_INTERVAL, intentExecuted);

    }

}


