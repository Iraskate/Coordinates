package com.example.coordinates.appusage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.widget.TextView;

import com.example.coordinates.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.AppOpsManager.MODE_ALLOWED;

public class AppUsageActivity extends AppCompatActivity {

    private TextView textViewUsageStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        textViewUsageStats = findViewById(R.id.usageStats);

        showAppUsage();

    }

    private void showAppUsage() {

        if(checkUsageStatsPermission())
            showUsageStats();
        else
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

    }

    private boolean checkUsageStatsPermission() {

        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), getPackageName());

        return mode == MODE_ALLOWED;

    }

    private void showUsageStats() {

        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, -1);

        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(), System.currentTimeMillis());

        StringBuilder statsData = new StringBuilder();

        for(int i = 0; i < queryUsageStats.size(); i++) {

            if(!convertDate(queryUsageStats.get(i).getLastTimeUsed()).equals("1970")) {

                statsData.append("Package Name: ").append(queryUsageStats.get(i).getPackageName()).append("\n")
                         .append("Last Time Used: ").append(convertDateTime(queryUsageStats.get(i).getLastTimeUsed())).append("\n")
                         .append("Describe Contents: ").append(queryUsageStats.get(i).describeContents()).append("\n")
                         .append("First Time Stamp: ").append(convertDateTime(queryUsageStats.get(i).getFirstTimeStamp())).append("\n")
                         .append("Last Time Stamp: ").append(convertDateTime(queryUsageStats.get(i).getLastTimeStamp())).append("\n")
                         .append("Total Time in Foreground: ").append(convertTime(queryUsageStats.get(i).getTotalTimeInForeground())).append("\n\n");

            }

        }

        textViewUsageStats.setText(statsData.toString());

    }

    public static String convertDate(long lastTimeUsed) {

        return (new SimpleDateFormat("yyyy", Locale.getDefault())).format(new Date(lastTimeUsed));

    }

    public static String convertTime(long lastTimeUsed) {

        long second = (lastTimeUsed / 1000) % 60;
        long minute = (lastTimeUsed / (1000 * 60)) % 60;
        long hour = (lastTimeUsed / (1000 * 60 * 60)) % 24;

        return String.format(Locale.getDefault(),"%02d:%02d:%02d", hour, minute, second);
    }

    public static String convertDateTime(long lastTimeUsed) {

        return (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())).format(new Date(lastTimeUsed));

    }

}
