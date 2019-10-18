package com.example.e_station;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;


import android.preference.PreferenceManager;
import android.util.Log;

public class StartActivity extends AppCompatActivity {

    private static String TAG = "StartActivity";

    public Fragment RegisterFragment = new RegisterFragment();
    public Fragment LoginFragment = new LoginFragment();

    public FragmentManager fm = getSupportFragmentManager();

    public int id;
    // 0 - RegisterFragment
    // 1 - LoginFragment

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int orientation = getResources().getConfiguration().orientation;
        Configuration configuration = this.getResources().getConfiguration();
        int smallScreenWi = configuration.smallestScreenWidthDp;

        id = prefs.getInt("id", 0);

        //landscape per cellulare
        if (orientation == Configuration.ORIENTATION_LANDSCAPE && smallScreenWi < 600) {
            Log.i(TAG, "Telefono landscape");

            if (id == 0) {
                fm.beginTransaction().replace(R.id.fragment, RegisterFragment).commit();
            } else {
                fm.beginTransaction().replace(R.id.fragment, LoginFragment).commit();
            }

        }

        // landscape per tablet
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE && smallScreenWi >= 600) {
            Log.i(TAG, "Tablet landscape");
            fm.beginTransaction().replace(R.id.fragment1, LoginFragment).commit();
            fm.beginTransaction().replace(R.id.fragment2, RegisterFragment).commit();
        }
        // portrait sia per telefono che per tablet
        else {
            Log.i(TAG, "Portrait");

            if (id == 0) {
                fm.beginTransaction().replace(R.id.fragment, RegisterFragment).commit();
            } else {
                fm.beginTransaction().replace(R.id.fragment, LoginFragment).commit();
            }
        }
    }

    @Override
    protected void onPause() {

        Log.i(TAG, "OnPause");

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("id", id);
        editor.commit();

        super.onPause();

    }

    @Override
    protected void onStop() {

        Log.i(TAG, "OnStop");

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            new AlertDialog.Builder(this)
            .setTitle("Attenzione")
                    .setMessage("Permessi non garantiti! L'app non potrà funzionare correttamente")
                    .setNegativeButton("CHIUDI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setIcon(R.drawable.ic_attenzione)
                    .show();
        }
    }
}
