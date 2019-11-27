package com.example.coordinates;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddBrandingActivity extends AppCompatActivity {

    private TextView locationId, locationLatitude, locationLongitude, locationTime, locationName;

    private EditText editTextAddLocationName;

    private Button buttonAddLocationName;

    private int position, timeSpentOnLocation;
    private  String latitude, longitude, name;

    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_branding);

        init();
        getPosition(savedInstanceState);
        printLocation();

        updateLocationName();

    }

    private void init() {

        locationId = findViewById(R.id.branding_location_id);
        locationLatitude = findViewById(R.id.branding_location_latitude);
        locationLongitude = findViewById(R.id.branding_location_longitude);
        locationTime = findViewById(R.id.branding_location_time);
        locationName = findViewById(R.id.branding_location_name);

        editTextAddLocationName = findViewById(R.id.editText_add_location_name);

        buttonAddLocationName = findViewById(R.id.button_update_location_name);

        myDb = new DatabaseHelper(this);

    }

    private void getPosition(Bundle savedInstanceState) {

        if(savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();

            this.position = extras.getInt("position");
            this.latitude = extras.getString("latitude");
            this.longitude = extras.getString("longitude");
            this.timeSpentOnLocation = extras.getInt("timeSpentOnLocation");
            this.name = extras.getString("locationName");

        }
        else {

            this.position = (int) savedInstanceState.getSerializable("position");
            this.latitude = (String) savedInstanceState.getSerializable("latitude");
            this.longitude = (String) savedInstanceState.getSerializable("longitude");
            this.timeSpentOnLocation = (int) savedInstanceState.getSerializable("timeSpentOnLocation");
            this.name = (String) savedInstanceState.getSerializable("locationName");

        }

    }

    @SuppressLint("SetTextI18n")
    private void printLocation() {

        locationId.setText("Location " + (position + 1) + ": ");
        locationLatitude.setText("Latitude: " + latitude);
        locationLongitude.setText("Longitude: " + longitude);
        locationTime.setText("Time on Location: " + timeSpentOnLocation + " minutes");

        if(name != null) {

            locationName.setVisibility(View.VISIBLE);
            locationName.setText("Location Name: " + name);

        }
        else
            locationName.setVisibility(View.INVISIBLE);

    }

    private void updateLocationName() {

        buttonAddLocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String locationName = editTextAddLocationName.getText().toString();

                if(!locationName.equals("")) {

                    int updated = myDb.updateLocationName(position, locationName);

                    if (updated > 0) {

                        //Finish previous and current activity
                        Intent finishIntent = new Intent("finish");
                        sendBroadcast(finishIntent);

                        finish();

                        //Open new Branding Activity
                        Intent intent = new Intent(AddBrandingActivity.this, BrandingActivity.class);
                        startActivity(intent);

                    }
                    else
                        showMessage("Update", "Failed");

                }
                else
                    showMessage("Error", "Add a name");
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
