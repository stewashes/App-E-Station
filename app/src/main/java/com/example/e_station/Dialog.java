package com.example.e_station;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Dialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {

    private static String TAG = "Dialog";

    private Spinner marche;
    private Spinner n_posti;
    private TextView avviso;

    private FirebaseDatabase mDatabase;
    private DatabaseReference reference;

    private String marca;
    private String numero_posti;
    private Location location;

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        MainActivity m = (MainActivity) getActivity();
        location = m.getLocation();

        builder.setView(view);
        builder.setTitle("Aggiungi colonnina");
        builder.setNegativeButton("Annulla", null);
        builder.setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aggiungiAlDatabase();
            }
        });

        initUI(view);

        initFirebase();

        return builder.create();
    }

    private void initUI(View view) {

        marche = (Spinner) view.findViewById(R.id.marche);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.marche, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marche.setAdapter(adapter1);
        marche.setOnItemSelectedListener(this);

        n_posti = (Spinner) view.findViewById(R.id.n_posti);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.numeri, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        n_posti.setAdapter(adapter2);
        n_posti.setOnItemSelectedListener(this);

        avviso = (TextView) view.findViewById(R.id.avviso);
    }

    private void initFirebase() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    private void aggiungiAlDatabase() {

        reference = mDatabase.getReference();

        StringBuilder lon = new StringBuilder(String.valueOf(location.getLongitude()));
        StringBuilder lat = new StringBuilder(String.valueOf(location.getLatitude()));

        lon.setCharAt(lon.indexOf("."), '-');
        lat.setCharAt(lat.indexOf("."), '-');

        String lon2 = lon.substring(0, lon.indexOf("-") + 4);
        String lat2 = lat.substring(0, lat.indexOf("-") + 4);

        String key = lon2 + " " + lat2;

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String via = addresses.get(0).getThoroughfare();
        String citta = addresses.get(0).getLocality();
        String stato = addresses.get(0).getCountryName();

        Colonnina c = new Colonnina(marca,
                numero_posti, via, citta, stato,
                location.getLatitude(), location.getLongitude(),
                location.getLatitude() + " " + location.getLongitude());

        reference.child("Colonnine").child(key).setValue(c);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.marche) {
            marca = parent.getItemAtPosition(position).toString();
        } else if (parent.getId() == R.id.n_posti) {
            numero_posti = parent.getItemAtPosition(position).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
