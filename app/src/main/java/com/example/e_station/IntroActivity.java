package com.example.e_station;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;

    Button btnNext;

    int position = 0;
    Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (restorePrefsData()) {
            Intent startActivity = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(startActivity);
            finish();
        }

        setContentView(R.layout.activity_intro);

        getSupportActionBar().hide();

        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);


        final List<ScreenItem> mList = new ArrayList<>();


        mList.add(new ScreenItem("Segnalazione stazione di ricarica", "Tramite il tap sul bottone '+' " +
                "sarà possibile aggiungere una nuova stazione di ricarica. Inoltre si potrà scegliere " +
                "la marca della colonnina e anche il numero di posti presenti.", R.drawable.aggiunta));

        mList.add(new ScreenItem("Eliminazione di una stazione di ricarica", "Tramite lo swipe verso destra " +
                "sull' elemento della lista sarà possibile eliminare dalla lista la colonnina scelta, questa operazione può " +
                "esser successivamente annullata grazie al messaggio che comparirà dopo l' avvenuta eliminazione.", R.drawable.eliminazione));


        mList.add(new ScreenItem("Visualizzazione sulla mappa", '\u00C9' + " possibile visulizzare la colonnina scelta" +
                " dalla lista direttamente sulla mappa questo grazie allo swipe verso sinistra sull' elemento della lista. Successivamente " +
                "si aprirà automaticamente la mappa centrata esattamente sulla stazione di ricarica scelta. ", R.drawable.visualizzazzione));

        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        tabIndicator.setupWithViewPager(screenPager);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();

                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                // quando ho raggiunto la fine
                if (position == mList.size() - 1) {

                    loadLastScreen();

                }
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startActivity = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(startActivity);

                saveSharedPrefs();
                finish();

            }
        });

    }

    private boolean restorePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpenedBefore = pref.getBoolean("isIntroOpened", false);
        return isIntroActivityOpenedBefore;
    }

    private void saveSharedPrefs() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.commit();
    }

    private void loadLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);

    }
}
