package com.example.e_station;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.e_station.rss_f.NewsService;

public class BootReceiver extends BroadcastReceiver {

    private static String TAG = "E_station BootReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.i(TAG, "onReceiver: Boot completato");

       Intent service = new Intent(context, NewsService.class);
       context.startService(service);

    }
}
