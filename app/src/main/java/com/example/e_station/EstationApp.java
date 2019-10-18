package com.example.e_station;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EstationApp extends Application {

    private static String TAG = "EstationApp";

    public static final String CHANNEL_ID = "NotificationService";

    public static long nCol = 0;

    private boolean  primo;

    private long feedMillis = -1;

    public void setFeedMillis(long feedMillis) {
        this.feedMillis = feedMillis;
    }

    public long getFeedMillis() {
        return feedMillis;
    }

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        primo = prefs.getBoolean("primo", false);

        createNotificationChannel();
        Log.i(TAG, "Creato channel per notifiche");


        numeroElementi();
        Log.i(TAG, "Numero elementi nel database: " + nCol);

    }

    private void numeroElementi() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Log.i(TAG, firebaseDatabase.toString());
        DatabaseReference ref = firebaseDatabase.getReference().child("Colonnine");
        Log.i(TAG, ref.toString());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nCol = dataSnapshot.getChildrenCount();
                Log.i(TAG, "valore letto al boot sul database: " + nCol);


                if(!primo){

                    Intent i = new Intent(getApplicationContext(), NotificationService.class);
                    primo = true;

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("primo", primo);
                    editor.commit();

                    Log.i(TAG, "onReceive: notificationService partito");

                    // devo fare questo if per differenziare le versioni
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getApplicationContext().startForegroundService(i);
                    } else {
                        getApplicationContext().startService(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationService Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


}