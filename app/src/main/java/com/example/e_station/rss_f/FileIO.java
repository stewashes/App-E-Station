package com.example.e_station.rss_f;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class FileIO {

    private static String TAG = "FileIO";

    private final String URL_STRING = "https://www.automoto.it/rss/news.xml";
    private final String FILENAME = "news_feed.xml";

    private Context context;

    public FileIO(Context context) {
        this.context = context;
    }

    public void downloadFile() {
        // get NetworkInfo object
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // if network is connected, download feed
        if (networkInfo != null &&
                networkInfo.isConnected()) {

            try {
                // get the URL
                URL url = new URL(URL_STRING);

                // get the input stream
                InputStream in = url.openStream();

                // get the output stream
                FileOutputStream out =
                        context.openFileOutput(FILENAME, Context.MODE_PRIVATE);

                // read input and write output
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                while (bytesRead != -1) {
                    out.write(buffer, 0, bytesRead);
                    bytesRead = in.read(buffer);
                }
                out.close();
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "Download File:" + e.toString());
            }
        }
    }

    public RSSFeed readFile() {
        try {
            // get the XML reader
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            // set content handler
            RSSFeedHandler theRssHandler = new RSSFeedHandler();
            xmlreader.setContentHandler(theRssHandler);

            // read the file from internal storage
            FileInputStream in = context.openFileInput(FILENAME);

            // parse the data
            InputSource is = new InputSource(in);
            xmlreader.parse(is);

            // set the feed in the activity
            RSSFeed feed = theRssHandler.getFeed();
            return feed;
        } catch (Exception e) {
            Log.e(TAG, "Read file: " + e.toString());
            return null;
        }
    }
}
