package com.example.coordinates;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;

public class BrandingActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private BroadcastReceiver broadcastReceiver;

    private DatabaseHelper myDb;

    private EditText searchList;
    private ListView locations;
    private ArrayList<String> listItem;
    private ArrayAdapter adapter;

    private String latitude, longitude, locationName;

    private int timeSpentOnLocation, positionOfElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branding);

        myDb = new DatabaseHelper(this);
        listItem = new ArrayList<>();

        searchList = findViewById(R.id.editText_search_minutes);
        locations = findViewById(R.id.branding_listview);

        viewData();
        viewListView();

        addBroadcastReceiver();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver);

    }

    private void viewData() {

        Cursor res = myDb.getAllData(DatabaseHelper.TABLE_NAME_2);

        while(res.moveToNext()) {

            if(res.getString(8) == null) {

                listItem.add("Location " + (res.getInt(7) + 1) + ": " + //Location number
                        "\n     " + /*DatabaseHelper.COL_2_2*/ "Latitude" + ": " + res.getString(1) + //Location latitude
                        "\n     " + /*DatabaseHelper.COL_3_2*/ "Longitude" + ": " + res.getString(2) + //Location longitude
                        "\n     " + /*DatabaseHelper.COL_7_2*/ "Time on location" + ": " + res.getString(6) + " minutes"); //Location time spent

            }
            else {

                listItem.add("Location " + (res.getInt(7) + 1) + ": " + //Location number
                        "\n     " + /*DatabaseHelper.COL_2_2*/ "Latitude" + ": " + res.getString(1) + //Location latitude
                        "\n     " + /*DatabaseHelper.COL_3_2*/ "Longitude" + ": " + res.getString(2) + //Location longitude
                        "\n     " + /*DatabaseHelper.COL_7_2*/ "Time on location"+ ": " + res.getString(6) + " minutes" + //Location time spent
                        "\n     " + /*DatabaseHelper.COL_9_2*/ "Location name" + ": " + res.getString(8)); //Location name

            }

        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItem);

        locations.setAdapter(adapter);

        searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                (BrandingActivity.this).adapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void viewListView() {

        locations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                positionOfElement = position;

                Cursor location = myDb.getDataByLocation(positionOfElement);

                while(location.moveToNext()) {

                    latitude = location.getString(1);
                    longitude = location.getString(2);
                    timeSpentOnLocation = location.getInt(6);
                    locationName = location.getString(8);

                }

                showPopup(view);

            }
        });

    }

    private void showPopup(View v) {

        PopupMenu popup = new PopupMenu(this, v);

        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_locations_menu);
        popup.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item1:
                showMap();
                return true;

            case R.id.item2:
                addBrandingName();
                return true;

            case R.id.item3:
                deleteLocation();
                return true;

            default:
                return false;

        }

    }

    private void showMap() {

        if(haveNetworkConnection()) {

            Intent googleMaps = new Intent(BrandingActivity.this, MapActivity.class);

            googleMaps.putExtra("latitude", this.latitude);
            googleMaps.putExtra("longitude", this.longitude);
            googleMaps.putExtra("timeSpentOnLocation", this.timeSpentOnLocation);

            googleMaps.putExtra("id", this.positionOfElement);

            startActivity(googleMaps);

        }
        else
            showMessage("Error", "No internet connection.");

    }

    private void addBrandingName() {

        Intent activity = new Intent(BrandingActivity.this, AddBrandingActivity.class);

        activity.putExtra("position", positionOfElement);
        activity.putExtra("latitude", latitude);
        activity.putExtra("longitude", longitude);
        activity.putExtra("timeSpentOnLocation", timeSpentOnLocation);
        activity.putExtra("locationName", locationName);

        startActivity(activity);

    }

    private void deleteLocation() {

        int i = 0;

        Cursor finalRes = myDb.getAllData(DatabaseHelper.TABLE_NAME_2);

        myDb.deleteDataByRowLocationID(positionOfElement);

        //Update all the location's position
        while(finalRes.moveToNext()) {

            int id = finalRes.getInt(0);

            myDb.updateLocationPosition(i, id);

            i++;

        }

        finalRes = myDb.getAllData(DatabaseHelper.TABLE_NAME_2);

        //Checks to see if there are any locations available
        if(finalRes.getCount() > 0) {

            adapter.clear();
            viewData();

        }
        else
            finish();

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

    private void addBroadcastReceiver() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if(action.equals("finish"))
                    finish();

            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("finish"));

    }

}
