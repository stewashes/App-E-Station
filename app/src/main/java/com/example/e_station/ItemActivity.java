package com.example.e_station;

import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemActivity extends AppCompatActivity implements OnClickListener {

    private static String TAG = "ItemActivity";

    private SimpleDateFormat dateOutFormat = new SimpleDateFormat("h:mm EEE dd/MM/YYYY", Locale.ITALY);

    private SimpleDateFormat dateInFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        getSupportActionBar().setTitle("Notizia");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references to widgets
        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        TextView pubDateTextView = (TextView) findViewById(R.id.pubDateTextView);
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        TextView linkTextView = (TextView) findViewById(R.id.linkTextView);

        // get the intent
        Intent intent = getIntent();

        // get data from the intent
        String pubDate = intent.getStringExtra("pubdate");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description").replace('\n', ' ');
        String link = intent.getStringExtra("link");

        // settaggio della data in un altro formato:
        Date date = null;
        try {
            date = dateInFormat.parse(pubDate.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String pubDateFormatted = dateOutFormat.format(date);

        // display data on the widgets
        pubDateTextView.setText(pubDateFormatted);
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        linkTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        linkTextView.setText(link);

        // set listener
        linkTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // get the intent
        Intent intent = getIntent();

        // get the Uri for the link
        String link = intent.getStringExtra("link");
        Uri viewUri = Uri.parse(link);

        if (ConnectivityHelper.controllaConnessione(getApplicationContext())) {
            Log.i(TAG, "connesso");
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, viewUri);
            PackageManager packageManager = getPackageManager();

            if (viewIntent.resolveActivity(packageManager) != null) {
                startActivity(viewIntent);
            } else {
                Toast.makeText(this, "Link non disponibile", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Link non disponibile");
            }

        } else {
            Log.i(TAG, "Non connesso");
            Toast.makeText(getApplicationContext(), "Non connesso ad internet", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = getIntent();

        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                try{
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("Text/plan");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Automoto.it");
                    String body = intent.getStringExtra("link");
                    i.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(i, "Condividi con: "));

                }catch (Exception e){
                    Toast.makeText(this, "Condivisione non disponibile", Toast.LENGTH_SHORT).show();
                }

        }

        return super.onOptionsItemSelected(item);
    }

}