package com.example.shepherd;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Intent displayTravelerOnMap;
    private Intent displayGuideOnMap;
    private Bundle travelerMapBundle;
    private Bundle guideMapBundle;
    private OnMapReadyCallback guideLocation;
    private OnMapReadyCallback travelerPosition;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

        double v= 31.675551;
        double v1=34.569365;

        try {defineDisplayTravelerOnMap(v,v1);}catch (Exception e){
            Log.d("ON MAP","skipped display traveler on map intent "); }
        try {defineDisplayGuideOnMap(v,v1);}catch (Exception e){
            Log.d("ON MAP","skipped display guide on map intent ");
        }


    }

    private void defineDisplayGuideOnMap(double v, double v1) {
        LatLng GuidePosition = new LatLng( v, v1);
        mMap.addMarker( new MarkerOptions().position( GuidePosition ).title( "Guide" ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( GuidePosition ) );
        guideMapBundle = new Bundle(  );
        displayGuideOnMap = new Intent(  );
    }

    private void defineDisplayTravelerOnMap(double v, double v1) {
    travelerMapBundle = new Bundle(  );

    displayTravelerOnMap = new Intent(  );
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in eldad home and move the camera
        LatLng eldadsHome = new LatLng( 31.675551, 34.569365 );
        mMap.addMarker( new MarkerOptions().position( eldadsHome ).title( "eldads home" ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( eldadsHome ) );
    }
}
