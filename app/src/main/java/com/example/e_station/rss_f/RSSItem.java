package com.example.e_station.rss_f;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class RSSItem {

    private static String TAG = "RSSitem";

    private String title = null;
    private String description = null;
    private String link = null;
    private String pubDate = null;

    private SimpleDateFormat dateOutFormat =
            new SimpleDateFormat("EEEE h:mm a (MMM d)", Locale.ITALY);

    private SimpleDateFormat dateInFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.US);

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPubDate() {
        return pubDate;
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