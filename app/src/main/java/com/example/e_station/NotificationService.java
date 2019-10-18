package com.example.e_station;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.e_station.EstationApp.CHANNEL_ID;
import static com.example.e_station.EstationApp.nCol;

public class NotificationService extends Service {

    private static String TAG = "NotificationService";

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference ref = firebaseDatabase.getReference().child("Colonnine");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    long nColonnine;
    long j;

    long i;

    private int notId = 2;

    private SharedPreferences prefs;

    @Override
    public void onCreate() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_ev_station_24px)
                    .setContentTitle("E-Station")
                    .setContentText("Servizio in esecuzione");

            Notification notification = builder.build();
            startForeground(1, notification);
        }

        Log.i(TAG, "onCreate");
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        j = nCol;
        Log.i(TAG, "valore preso da EstationApp: " + j);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");
        i = 0;

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        nColonnine = prefs.getLong("nColonnine", j);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.i(TAG, "ChildEventListener - onChildAdded");

                i++;
                Log.i(TAG, "Valore di i prima del if: " + i);
                Log.i(TAG, "Valore di nColonnine prima del if: " + nColonnine);

                if (i > nColonnine) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                        if( mAuth.getCurrentUser() != null ){

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_baseline_ev_station_24px)
                                    .setContentTitle("E-Station")
                                    .setContentText("Aggiunta una nuova colonnina")
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                            notificationManager.notify(notId, builder.build());

                            notId++;
                        }

                    } else {

                        if( mAuth.getCurrentUser() != null ) {
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.ic_baseline_ev_station_24px)
                                    .setContentTitle("E-Station")
                                    .setContentText("Aggiunta una nuova colonnina")
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                            notificationManager.notify(notId, builder.build());

                            Log.i(TAG, "idNot: " + notId);
                            notId++;
                        }

                    }

                    nColonnine++;
                    Log.i(TAG, "Valore di nColonnine : " + nColonnine);

                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("nColonnine", nColonnine);
                editor.commit();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "ChildEventListener - onChildChanged");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "ChildEventListener - onChildRemoved");

                i--;
                Log.i(TAG, "Valore di i: " + i);
                nColonnine--;
                Log.i(TAG, "Valore di nColonnine : " + nColonnine);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("nColonnine", nColonnine);
                editor.commit();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "ChildEventListener - onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "ChildEventListener - onCancelled");
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");

        return null;
    }

}

