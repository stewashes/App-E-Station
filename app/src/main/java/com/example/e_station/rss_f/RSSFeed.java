package com.example.e_station.rss_f;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")

public class RSSFeed {

    private String title = null;
    private String pubDate = null;
    private ArrayList<RSSItem> items;

    private SimpleDateFormat dateOutFormat =
            new SimpleDateFormat("EEE h:mm a (MMM d)", Locale.ITALY);

    private SimpleDateFormat dateInFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.US);

    public RSSFeed() {
        items = new ArrayList<RSSItem>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPubDate() {
        return pubDate;
    }

    public long getPubDateMillis() {
        Date date = new Date(0);
        try {
            date = dateInFormat.parse(pubDate.trim());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long dateMillis = date.getTime();
        return dateMillis;
    }

    public int addItem(RSSItem item) {
        items.add(item);
        return items.size();
    }

    public RSSItem getItem(int index) {
        return items.get(index);
    }

    public ArrayList<RSSItem> getAllItems() {
        return items;
    }

    public String getPubDateFormatted() {
        try {
            Date date = dateInFormat.parse(pubDate.trim());
            String pubDateFormatted = dateOutFormat.format(date);
            return pubDateFormatted;
        } catch (Exception e) {
            // throw new RuntimeException(e);
        }
        return "";
    }
}