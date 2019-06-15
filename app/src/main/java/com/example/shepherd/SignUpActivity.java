package com.example.shepherd;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    private static final String TAG ="" ;
    List<AuthUI.IdpConfig> providers = Collections.singletonList(
            new AuthUI.IdpConfig.GoogleBuilder().build() );
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private android.widget.ImageView backgroundImage;
    private View mContentView;
    private RadioButton traveler;
    private RadioButton guide;
    private TextView email;
    private TextView name;
    private TextView phone;
    private TextView uid;
    private String userName;
    private String userEmail;
    private String userPhoneNum;
    private String userId;
    private int uIdNumber;
    private Intent sendDataToFireBase;
    private Intent goToMainScreen;
    private Bundle signUpBundle;
    private double latitude,longitude;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedPrefEditor;
    private FirebaseAuth mAuth;
    private FirebaseUser currentFBUser;
    private Button signInBtn;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility( View.VISIBLE );
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide( AUTO_HIDE_DELAY_MILLIS );
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );
        currentFBUser = getCurrentUser();
        backgroundImage = findViewById( R.id.backgroundimagelogo );
        startActivityForResult( AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders( providers ).build(), 0 );
        mVisible = true;
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mControlsView = findViewById( R.id.fullscreen_content_controls );
        mContentView = findViewById( R.id.fullscreen_content );
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        } );
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        name = findViewById( R.id.user_name_txt_fld );
        email = findViewById( R.id.user_email_txt_fld );
        phone = findViewById( R.id.user_phone_txt_fld );
        uid = findViewById( R.id.user_id_number_txt_fld );
        traveler = findViewById( R.id.traveler_rd_btn );
        guide = findViewById( R.id.guide_rd_btn );
        //fill in text fileds with user data
        name.setText( mAuth.getCurrentUser().getDisplayName() );
        email.setText( mAuth.getCurrentUser().getEmail() );
        phone.setText( mAuth.getCurrentUser().getPhoneNumber() );
        uid.setText( mAuth.getCurrentUser().getUid() );
        sharedPref = getSharedPreferences( "userSharedPreferdLocalData", Context.MODE_PRIVATE );
        //add data to shard prefer
        addPreferdUserData( "username",userName );
        addPreferdUserData( "user email",userEmail );
        addPreferdUserData( "user phone",userPhoneNum );
        addPreferdUserData( "user ID",userId );
        signInBtn = findViewById( R.id.sign_up_btn );
        signInBtn.setOnClickListener(this);
        defineDataBundleToSend();
       // signInBtn.setOnTouchListener( mDelayHideTouchListener );
        try {
            defineGoToMainScreenIntent();
        }catch (Exception e){
            Log.d( "sign int","cant sign in",e.getCause() );
        }


    }

    private void defineDataBundleToSend() {
        signUpBundle = new Bundle(  );
        signUpBundle.putString( "id",userId );
        signUpBundle.putString( "name", userName );
        signUpBundle.putString( "phone", userPhoneNum );
      //  signUpBundle.putString( "latitude", lat);
      //  signUpBundle.putString( "Longitude", lon);
    }

    private void addPreferdUserData (String key,String value){
        sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putString( key,value );
        sharedPrefEditor.apply();
    }
    private String recivePreferdUserData(String key){
        sharedPref=getSharedPreferences( key,Context.MODE_PRIVATE );
        return sharedPref.getString(key,"not found"  );
    }

    private void defineGoToMainScreenIntent() {
                goToMainScreen = new Intent( SignUpActivity.this,MapsActivity.class );
                startActivity( goToMainScreen );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == 0) {
            if (resultCode == 0) {
            } else {
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate( savedInstanceState );

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide( 100 );
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility( View.GONE );
        mVisible = false;
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks( mShowPart2Runnable );
        mHideHandler.postDelayed( mHidePart2Runnable, UI_ANIMATION_DELAY );
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION );
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks( mHidePart2Runnable );
        mHideHandler.postDelayed( mShowPart2Runnable, UI_ANIMATION_DELAY );

    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks( mHideRunnable );
        mHideHandler.postDelayed( mHideRunnable, delayMillis );
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateUI( currentUser );


    }
    //todo: from here make the move to main app screen
    private void updateUI(FirebaseUser currentUser) {
        email.setText( currentUser.getEmail() );
        name.setText( currentUser.getDisplayName() );
        phone.setText( currentUser.getPhoneNumber() );
    //    uIdNumber = Integer.parseInt (currentUser.getUid());
  //      uid.setText( uIdNumber );
    }

    private void createAccount(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d( TAG, "createUserWithEmail:success" );
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI( user );
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w( TAG, "createUserWithEmail:failure", task.getException() );
                    Toast.makeText( SignUpActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT ).show();
                    updateUI( null );
                }
            }
        });}

    private void signInWithEmailAndPassword(String email, String password) {
                mAuth.signInWithEmailAndPassword( email, password )
                        .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d( TAG, "signInWithEmail:success" );
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI( user );
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w( TAG, "signInWithEmail:failure", task.getException() );
                                    Toast.makeText( SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT ).show();
                                    updateUI( null );
                                }
                            }
                        } );
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
            case R.id.sign_up_btn:

            startActivity( goToMainScreen );
                break;
        }
    }
}
