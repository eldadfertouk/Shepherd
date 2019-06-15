package com.example.shepherd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.shepherd.DataBaseObjects.ReadFromDB;
import com.example.shepherd.DataBaseObjects.SendDataToFireBase;

public class MainActivity extends Activity implements View.OnClickListener {

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 31.675568, wayLongitude = 34.569321;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private android.widget.Button btnLocation;
    private android.widget.Button btnContinueLocation;
    private android.widget.Button startButton;
    private android.widget.Button readFromDb;
    private android.widget.Button sendToDb;


    private StringBuilder stringBuilder;
    private boolean isContinue = false;
    private boolean isGPS = false;

    private static Intent signUp;
    private String userId;
    private String userName;
    private String userPhone;
    private String introMassage;

    private TextView userIdtxtfld;
    private TextView txtContinueLocation;
    private TextView txtLocation;


    private View.OnClickListener start;

    private FirebaseUser currentUser;

    protected Bundle signBundle;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        currentUser = getCurrentUser();
        userId = currentUser.getUid();
        userName = currentUser.getDisplayName();
        userPhone = currentUser.getPhoneNumber();

        //show toast with user id
        Toast.makeText( this, "user id:" + currentUser.getUid(), Toast.LENGTH_LONG ).show();
        //define layout
        this.userIdtxtfld = (TextView) findViewById( R.id.user_id_number_txt_fld );
        this.txtContinueLocation = (TextView) findViewById( R.id.txtContinueLocation );
        this.btnContinueLocation = (Button) findViewById( R.id.btnContinueLocation );
        this.txtLocation = (TextView) findViewById( R.id.txtLocation );
        this.btnLocation = (Button) findViewById( R.id.btnLocation );
        this.sendToDb = (Button) findViewById( R.id.senddatabtn );
        this.readFromDb = (Button) findViewById( R.id.readdatabtn );
        TextView welcomeMsg = findViewById( R.id.welcome_msg );
        welcomeMsg.setText( introMassage );

        startButton = findViewById( R.id.start_btn );
        startButton.setOnClickListener( this );

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );
        locationRequest = LocationRequest.create();
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval( 10 * 1000 ); // 10 seconds
        locationRequest.setFastestInterval( 5 * 1000 ); // 5 seconds

        new GpsUtils( this ).turnGPSOn( new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        } );

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            txtLocation.setText( String.format( Locale.ENGLISH, "%s - %s", wayLatitude, wayLongitude ) );
                        } else {
                            stringBuilder.append( wayLatitude );
                            stringBuilder.append( "-" );
                            stringBuilder.append( wayLongitude );
                            stringBuilder.append( "\n\n" );
                            txtContinueLocation.setText( stringBuilder.toString() );
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates( locationCallback );
                        }
                    }
                }
            }
        };

        btnLocation.setOnClickListener( this );
        btnContinueLocation.setOnClickListener( this );
        startButton.setOnClickListener( this );
        readFromDb.setOnClickListener( this );
        sendToDb.setOnClickListener( this );
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                txtLocation.setText(String.format( Locale.US, "%s - %s", wayLatitude, wayLongitude));
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    public MainActivity() {
        introMassage = "welcome to Shepherd app Click On Start To Start";
    }

    private void defineSignInIntent(String userId,String username,String userphone,double wayLatitude,double wayLongitude) {
        String lat = Double.toString( wayLatitude );
        String lon = Double.toString(wayLongitude  );
        signBundle = new Bundle();
        signBundle.putString( "id",userId );
        signBundle.putString( "name", username );
        signBundle.putString( "phone", userphone );
        signBundle.putString( "latitude", lat);
        signBundle.putString( "Longitude", lon);
        signUp = new Intent( MainActivity.this, SignUpActivity.class );
        signUp.putExtras( signBundle );
       // startActivity( signUp, signBundle );

    }
    private FirebaseUser getCurrentUser () {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

        return user;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.senddatabtn:
                Toast.makeText( this,"send data",Toast.LENGTH_SHORT ).show();
                break;
            case R.id.start_btn:
                defineSignInIntent(userId,userName,userPhone,wayLatitude,wayLongitude);
                Toast.makeText( this,"move to sign in screen with lang and long"+wayLatitude+"--"+wayLongitude,Toast.LENGTH_SHORT ).show();
                break;
            case R.id.readdatabtn :
                Toast.makeText( this,"READ READ",Toast.LENGTH_SHORT ).show();
                break;
            case R.id.btnContinueLocation:
                if (!isGPS) {
                    Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }
                isContinue = true;
                stringBuilder = new StringBuilder();
                getLocation();
                break;
            case R.id.btnLocation:
                if (!isGPS) {
                    Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }
                isContinue = false;
                getLocation();
                break;
        }

    }

}


