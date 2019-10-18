package com.example.e_station;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public LocationManager locationManager;

    private LocationRequest locationRequest;
    private GoogleApiClient locationClient;

    final MapsFragment MapsFragment = new MapsFragment();
    final Fragment RechargeFragment = new RechargeFragment();
    final Fragment NewsFragment = new ItemsFragment();

    Fragment fragmentAttivo = RechargeFragment;

    final FragmentManager fm = getSupportFragmentManager();

    private Location loc;

    private static String TAG = "MainActivity";

    private FirebaseAuth mAuth;

    private ArrayList<Colonnina> lista;

    BottomNavigationView bv;

    // getter / setter per Lista
    public ArrayList<Colonnina> getLista() {
        return this.lista;
    }

    public void setLista(ArrayList<Colonnina> lista) {
        this.lista = lista;
    }

    // getter pe location
    public Location getLocation() {
        return loc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");

        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bv = (BottomNavigationView) findViewById(R.id.nav_view);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Apre le impostazioni per accendere il gps
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Attivare il GPS per utilizzare l' applicazione", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        locationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(2000);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            loc = LocationServices.FusedLocationApi.getLastLocation(locationClient);
        }

        lista = new ArrayList<>();

        fm.beginTransaction().add(R.id.container2, NewsFragment, "3").hide(NewsFragment).commit();
        fm.beginTransaction().add(R.id.container2, MapsFragment, "2").hide(MapsFragment).commit();
        fm.beginTransaction().add(R.id.container2, RechargeFragment, "1").commit();

        bv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_mappa:
                        if (fragmentAttivo != MapsFragment) {
                            MapsFragment.setMyPosition();
                            fm.beginTransaction().hide(fragmentAttivo).show(MapsFragment).commit();
                            fragmentAttivo = MapsFragment;
                        }
                        return true;

                    case R.id.nav_recharge:
                        fm.beginTransaction().hide(fragmentAttivo).show(RechargeFragment).commit();
                        fragmentAttivo = RechargeFragment;
                        return true;

                    case R.id.nav_news:
                        fm.beginTransaction().hide(fragmentAttivo).show(NewsFragment).commit();
                        fragmentAttivo = NewsFragment;
                        return true;
                }
                return false;
            }
        });

        // Setto l'elemto 'selezionato' graficamente
        bv.setSelectedItemId(R.id.nav_recharge);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

        locationClient.connect();
    }

    @Override
    protected void onStop() {

        Log.i(TAG, "onStop");
        locationClient.disconnect();

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Log.i(TAG, "OnSaveInstanceState");

        savedInstanceState.putParcelable("location", loc);
        savedInstanceState.putInt("SelectedItemId", bv.getSelectedItemId());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i(TAG, "OnRestoreInstanceState");

        loc = savedInstanceState.getParcelable("location");
        int selectedItemId = savedInstanceState.getInt("SelectedItemId");
        bv.setSelectedItemId(selectedItemId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i(TAG, "OnOptionsItemSelected");

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.profile:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);

                break;
            case R.id.logout:
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Vuoi effettuare il Logout?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mAuth.signOut();

                                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                                startActivity(intent);
                                // tolto se no rientrando l app crasha
                                // finish();

                            }
                        })
                        .setNegativeButton("NO", null)
                        .setIcon(R.drawable.ic_attenzione)
                        .show();
                break;

        }



        return true;
    }

    @Override
    public void onConnected(Bundle dataBundle) {

        Log.i(TAG, "onConnected");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(locationClient);
            if (location != null) {
                loc = location;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG, "onConnectionSuspended");

        if (locationClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i(TAG, "onConnectionFailed");

        Toast.makeText(this, "Connection failed! " +
                        "Please check your settings and try again.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(TAG, "onLocationChanged");

        if (location != null) {
            Log.i(TAG, "onLocationChanged - salvataggio location nella mia variabile");
            loc = location;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);

    }
}
