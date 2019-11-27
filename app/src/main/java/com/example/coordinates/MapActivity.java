package com.example.coordinates;

import androidx.fragment.app.FragmentActivity;

import android.database.Cursor;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private final static int MAX_GREEN_TIME = 15; //Minutes
    private final static int MAX_ORANGE_TIME = 35; //Minutes

    private DatabaseHelper myDb;

    private int positionOfElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        myDb = new DatabaseHelper(this);

        getExtrasValues(savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        if(mapFragment != null)
            mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        setUiSettings(googleMap);
        setMarkers(googleMap);

    }

    private void getExtrasValues(Bundle savedInstanceState) {

        if(savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();

            this.positionOfElement = extras.getInt("id");

        }
        else {

            this.positionOfElement = (int) savedInstanceState.getSerializable("id");

        }

    }

    private void setUiSettings(GoogleMap googleMap) {

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

    }

    private void setMarkers(GoogleMap googleMap) {

        String latitude, longitude, locationName;
        int timeSpentOnLocation;

        Cursor res;

        //Checks if the user wants to see all locations on the map or a specific one
        if(positionOfElement == -1)
            res = myDb.getAllData(DatabaseHelper.TABLE_NAME_2);
        else
            res = myDb.getDataByLocation(positionOfElement);

        MarkerOptions markerOptions;

        while (res.moveToNext()) {

            latitude = res.getString(1);
            longitude = res.getString(2);
            timeSpentOnLocation = res.getInt(6);
            locationName = res.getString(8);

            LatLng currentPosition = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

            if(locationName != null)
                markerOptions = new MarkerOptions().position(currentPosition).title(locationName).snippet((timeSpentOnLocation + " minutes"));
            else
                markerOptions = new MarkerOptions().position(currentPosition).title((timeSpentOnLocation + " minutes"));

            //Add color based on how long the person was on that location
            if(timeSpentOnLocation <= MAX_GREEN_TIME)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            else if(timeSpentOnLocation <= MAX_ORANGE_TIME)
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            else
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentPosition));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 9));

            googleMap.addMarker(markerOptions);

        }

    }
}