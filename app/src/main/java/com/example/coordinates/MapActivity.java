package com.example.coordinates;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getCoordinates(savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        if(mapFragment != null)
            mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("MapLat", latitude);
        Log.d("MapLong", longitude);

        LatLng currentPosition = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

        MarkerOptions markerOptions = new MarkerOptions().position(currentPosition).title(("Current Position"));

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentPosition));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 5));

        googleMap.addMarker(markerOptions);

    }

    private void getCoordinates(Bundle savedInstanceState) {

        if(savedInstanceState == null) {

            Bundle extras = getIntent().getExtras();

            this.latitude = extras.getString("latitude");
            this.longitude = extras.getString("longitude");

        }
        else {

            this.latitude = (String) savedInstanceState.getSerializable("latitude");
            this.longitude = (String) savedInstanceState.getSerializable("longitude");

        }

    }
}