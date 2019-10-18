package com.example.e_station.rss_f;

import android.app.Application;
import android.util.Log;

public class NewsApp extends Application {

    //TODO: spostare in EstaionApp

    private static String TAG = "NewsApp";

    private long feedMillis = -1;

    public void setFeedMillis(long feedMillis) {
        this.feedMillis = feedMillis;
    }

    public long getFeedMillis() {
        return feedMillis;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "App partita");
    }
}