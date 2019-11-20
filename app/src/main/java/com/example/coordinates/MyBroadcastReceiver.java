package com.example.coordinates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        //if(ACTION.equals(intent.getAction())) {

            //Intent serviceIntent = new Intent(context, LocationService.class);

            //ContextCompat.startForegroundService(context, serviceIntent);

       // }

    }

}

