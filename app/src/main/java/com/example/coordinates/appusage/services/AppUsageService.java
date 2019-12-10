package com.example.coordinates.appusage.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.coordinates.R;
import com.example.coordinates.appusage.activities.AppUsageActivity;
import com.example.coordinates.location.activities.LocationActivity;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.coordinates.main.App.CHANNEL_1_ID;
import static com.example.coordinates.main.App.CHANNEL_2_ID;

public class AppUsageService extends Service {

    private final static double SHOW_NOTIFICATION_AFTER_HOURS = 0;

    private NotificationManagerCompat notificationManager;

    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {

        notificationManager = NotificationManagerCompat.from(this);

        initServiceNotification();
        initWakeLock();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("App Usage Service", "Started");

        mWakeLock.acquire();

        showNotification();

        stopSelf();

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.d("App Usage Service", "Destroyed");

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
                .setContentTitle("App Usage Service")
                .setContentText("App Usage Running")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

    }

    private void initNotification(String title, int notificationNumber) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle("Long app usage")
                .setContentText("You are using " + title + " for too long!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(notificationNumber, notification);

    }

    private void showNotification() {

        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, -1);

        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(), System.currentTimeMillis());

        for(int i = 0; i < queryUsageStats.size(); i++) {

            if(!AppUsageActivity.convertDate(queryUsageStats.get(i).getLastTimeUsed()).equals("1970")) {

                String packageName = queryUsageStats.get(i).getPackageName();

                //Gets app name and then capitalizes the first letter
                packageName = packageName.substring(packageName.lastIndexOf(".") + 1);
                packageName = packageName.substring(0, 1).toUpperCase() + packageName.substring(1);

                //Convert milliseconds into hours
                long hours = TimeUnit.MILLISECONDS.toHours(queryUsageStats.get(i).getTotalTimeInForeground());

                if(hours >= SHOW_NOTIFICATION_AFTER_HOURS)
                    initNotification(packageName, i + 3);

            }

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
