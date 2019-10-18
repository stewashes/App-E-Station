package com.example.e_station.rss_f;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.e_station.EstationApp;

import java.util.Timer;
import java.util.TimerTask;

public class NewsService extends Service {

    private static String TAG = "NewsService";

    private EstationApp app;
    private Timer timer;
    private FileIO io;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate - Service created");
            app = (EstationApp) getApplication();
        io = new FileIO(getApplicationContext());
        startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service bound - not used!");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service destroyed");
        stopTimer();
    }

    private void startTimer() {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                Log.i(TAG, "StartTimer - Timer task started");

                io.downloadFile();
                Log.i(TAG, "StartTimer - File downloaded");

                RSSFeed newFeed = io.readFile();
                Log.i(TAG, "StartTimer - File read");

                // if new feed is newer than old feed
                if (newFeed.getPubDateMillis() > app.getFeedMillis()) {
                    Log.i(TAG, "StartTimer - Nuove notizie disponibili");

                    // update app object
                    app.setFeedMillis(newFeed.getPubDateMillis());

                }
            }
        };

        timer = new Timer(true);
        int delay = 1000 * 10;           // 10 secondi
        int interval = 1000 * 60 * 30;   // 30 minuti
        timer.schedule(task, delay, interval);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
