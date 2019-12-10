package com.example.coordinates.main;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.coordinates.location.services.LocationService;

import java.util.Calendar;
import java.util.Date;

public class SchedulerSetupReceiver extends BroadcastReceiver {

    private static final String APP_TAG = "com.example.coordinates";
    private static final int EXEC_INTERVAL = 10000;

    public static Date dateTimeExecuted;
    public static AlarmManager alarmManager;
    public static PendingIntent intentExecuted;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(APP_TAG, "SchedulerSetupReceiver.onReceive() called");

        dateTimeExecuted = getCurrentDatePlusDays(7);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, SchedulerEventReceiver.class);

        intentExecuted = PendingIntent.getBroadcast(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), EXEC_INTERVAL, intentExecuted);

    }

    private Date getCurrentDatePlusDays(int days) {

        Calendar cal = Calendar.getInstance();

        Log.d("Current Time", String.valueOf(cal.getTime())); //Current DateTime

        cal.add(Calendar.DATE, days);

        Log.d("Current Time +", String.valueOf(cal.getTime())); //Current DateTime + time given

        return cal.getTime();

    }

}


