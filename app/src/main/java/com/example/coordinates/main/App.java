package com.example.coordinates.main;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_1_ID = "service_channel";
    public static final String CHANNEL_2_ID = "app_usage_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    private void createNotificationChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            notificationChannel1.setDescription("Service Running");

            NotificationChannel notificationChannel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "App Usage",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel2.setDescription("You are using too much the app.");

            NotificationManager manager = getSystemService(NotificationManager.class);

            if(manager != null) {

                manager.createNotificationChannel(notificationChannel1);
                manager.createNotificationChannel(notificationChannel2);

            }

        }

    }

}



