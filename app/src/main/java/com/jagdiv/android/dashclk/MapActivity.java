package com.jagdiv.android.dashclk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapp);
        mMap = mapFragment.getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // mapFragment.getMapAsync(this);
        Intent myIntent = getIntent(); // gets the previously created intent
        double latit = Double.parseDouble(myIntent.getStringExtra("latitude"));
        double longit=  Double.parseDouble(myIntent.getStringExtra("longitude"));
        LatLng addresslatlng = new LatLng(latit, longit);
        mMap.addMarker(new MarkerOptions().position(addresslatlng).title("Marker in "+myIntent.getStringExtra("locality")));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(addresslatlng));
        // CameraUpdate update = CameraUpdateFactory.newLatLngZoom(addresslatlng, 15);
        // mMap.moveCamera(update);

        System.out.println("22222222222222nd type");

    }
}
