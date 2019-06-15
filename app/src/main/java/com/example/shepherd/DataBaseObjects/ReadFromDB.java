package com.example.shepherd.DataBaseObjects;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError.*;
import static android.support.constraint.Constraints.TAG;


public class ReadFromDB {
    private void getDataFromDataBase (DatabaseReference dbRef){
        dbRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG,"value is"+value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.w(TAG,"failed to read value from DB", databaseError.toException());
            }
        } );
    }
}
