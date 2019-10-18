package com.example.e_station;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.e_station.rss_f.FileIO;
import com.example.e_station.rss_f.RSSFeed;
import com.example.e_station.rss_f.RSSItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static String TAG = "ItemsFragment";

    private EstationApp app;
    private RSSFeed feed;
    private long feedPubDateMillis;
    private FileIO io;

    private TextView titleTextView;
    private ListView itemsListView;

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.i(TAG, "OnCreateView");

        view = inflater.inflate(R.layout.fragment_items, container, false);

        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        itemsListView = (ListView) view.findViewById(R.id.itemsListView);

        itemsListView.setOnItemClickListener(this);

        // get references to Application and FileIO objects
        Log.i(TAG, "onCreateView - app");
        app = (EstationApp) getActivity().getApplication();
        Log.i(TAG, "onCreateView - io");
        io = new FileIO(getActivity().getApplicationContext());

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "OnResume");

        // get feed from app object
        feedPubDateMillis = app.getFeedMillis();

        if (feedPubDateMillis == -1) {
            Log.i(TAG, "OnResume - download, read, display");
            new DownloadFeed().execute();   // download, read, and display
        } else if (feed == null) {
            Log.i(TAG, "OnResume - read, display");
            new ReadFeed().execute();       // read and display
        } else {
            Log.i(TAG, "OnResume - display");
            updateDisplay();                // just display
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    class DownloadFeed extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            io.downloadFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "Feed downloaded");
            new ReadFeed().execute();
        }
    }

    class ReadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            feed = io.readFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "Feed lette");
            if (feed != null) {
                app.setFeedMillis(feed.getPubDateMillis());
                ItemsFragment.this.updateDisplay();

            }
        }
    }

    public void updateDisplay() {

        if (feed == null) {
            titleTextView.setText("Non riesco a trovare notizie");
            return;
        }
        Log.i(TAG, "updateDisplay");

        // set the title for the feed
        titleTextView.setText(feed.getTitle());

        // get the items for the feed
        List<RSSItem> items = feed.getAllItems();

        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        for (RSSItem item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("date", item.getPubDateFormatted());
            map.put("title", item.getTitle());
            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.listview_item;
        String[] from = {"date", "title"};
        int[] to = {R.id.pubDateTextView, R.id.titleTextView};

        // create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, resource, from, to);
        itemsListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        RSSItem item = feed.getItem(position);

        Intent intent = new Intent(getActivity(), ItemActivity.class);
        intent.putExtra("pubdate", item.getPubDate());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("link", item.getLink());

        this.startActivity(intent);
    }
}
