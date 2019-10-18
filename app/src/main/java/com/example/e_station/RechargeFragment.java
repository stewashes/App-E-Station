package com.example.e_station;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RechargeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static String TAG = "RechargeFragment";

    private View view;

    private AdapterColonnine adapterColonnine;
    private RecyclerView recyclerView;
    private FloatingActionButton aggiungi;
    private EditText cerca;
    private ArrayList<Colonnina> lista;
    private ArrayList<Colonnina> colonnine;
    private LinearLayout linearLayout;
    private Spinner ricerca_spinner;
    private String text;

    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_recharge, container, false);

        ricerca_spinner = view.findViewById(R.id.ricerca_spinner);
        ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(getContext(), R.array.ricerca_spinner, android.R.layout.simple_spinner_item);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ricerca_spinner.setAdapter(ad);

        ricerca_spinner.setOnItemSelectedListener(this);

        cerca = (EditText) view.findViewById(R.id.cerca);

        cerca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    search(s.toString(),text);
                } else {
                    search("",text);
                }
            }
        });

        aggiungi = (FloatingActionButton) view.findViewById(R.id.aggiungi);
        aggiungi.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.lista_col);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //lista per aggiungere

        // Riferimento alla MainActivity
        final MainActivity m = (MainActivity) getActivity();
        // Collegamento alla MapsActivity
        final MapsFragment mf = (MapsFragment) m.MapsFragment;
        lista = m.getLista();

        //lista per eliminare
        colonnine = new ArrayList<Colonnina>();

        reference = FirebaseDatabase.getInstance().getReference().child("Colonnine");

        swipe();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                lista.clear();
                mf.cancellaMarker();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    long n_col = dataSnapshot.getChildrenCount();
                    Log.i(TAG, n_col + " ");

                    Colonnina c = dataSnapshot1.getValue(Colonnina.class);
                    lista.add(c);

                    // Per aggiungere il marker alla mappa richiamo la funzione presente in MapsFragment
                    mf.placeMarker(c.getMarca(), c.getLatitudine(), c.getLongitudine(), c.getN_posti());
                }

                //setto a lista definita nella mainactivity
                m.setLista(lista);

                adapterColonnine = new AdapterColonnine(getContext(), lista);
                recyclerView.setAdapter(adapterColonnine);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Qualcosa non va...", Toast.LENGTH_SHORT).show();
            }
        });

        linearLayout = view.findViewById(R.id.recharge);

        return view;
    }

    private void search(String s, String text) {

        String key = "";

        if(text.equals("Marca"))
            key = "marca";
        else
            key = "citta";


        if (s.length() != 0) {
            s = s.toLowerCase();
            s = s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        Query query = reference.orderByChild(key).startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                lista.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Colonnina c = dataSnapshot1.getValue(Colonnina.class);
                    lista.add(c);

                }

                adapterColonnine = new AdapterColonnine(getActivity(), lista);
                recyclerView.setAdapter(adapterColonnine);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Qualocosa non va", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // controllo connessione ad internet
        if(ConnectivityHelper.controllaConnessione(getContext())){
            Log.i(TAG, "connesso");
            openDialog();

        } else {
            Log.i(TAG, "Non connesso");
            Toast.makeText(getContext(),"Non connesso ad internet", Toast.LENGTH_LONG).show();
        }

    }

    private void openDialog() {
        Dialog dialog = new Dialog();
        dialog.show(getFragmentManager(), "dialog");


    }

    public void swipe(){

        //Swipe a Destra!
        ItemTouchHelper.SimpleCallback itemCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                // final Colonnina[] col = new Colonnina[1];
                Log.i(TAG, position + " ");

                Double latitudine = lista.get(position).getLatitudine();
                Double longitudine = lista.get(position).getLongitudine();

                Log.i(TAG, latitudine + " " + longitudine);

                Query mQuery = reference.orderByChild("coordinate").equalTo(latitudine + " " + longitudine);

                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        colonnine.clear();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            Log.i(TAG, "entrato nel for per l 'eliminazione");

                            Colonnina c = dataSnapshot1.getValue(Colonnina.class);
                            colonnine.add(c);
                            dataSnapshot1.getRef().removeValue();

                            MainActivity m = ((MainActivity) getActivity());
                            MapsFragment mf = (MapsFragment) m.MapsFragment;
                            mf.cancellaMarker();
                        }


                        showSnackbar(colonnine);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.RED))
                        .addActionIcon(R.drawable.ic_delete_white_24dp)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(itemCallback).attachToRecyclerView(recyclerView);

        //Swipe a Sinistra!
        ItemTouchHelper.SimpleCallback itemCallback2 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                // final Colonnina[] col = new Colonnina[1];

                Double latitudine = lista.get(position).getLatitudine();
                Double longitudine = lista.get(position).getLongitudine();

                // Riferimento alla MainActivity
                final MainActivity m = (MainActivity) getActivity();
                // Collegamento alla MapsActivity
                final MapsFragment mf = (MapsFragment) m.MapsFragment;

                LatLng coordinate = new LatLng(latitudine, longitudine);

                mf.moveCamera(coordinate, 17);

                // cambio di schermata
                m.fm.beginTransaction().hide(m.fragmentAttivo).show(mf).commit();
                m.fragmentAttivo = mf;

                // per settare il bottone della navbar sul bottone della mappa
                m.bv.setSelectedItemId(R.id.nav_mappa);

                adapterColonnine = new AdapterColonnine(getActivity(), lista);
                recyclerView.setAdapter(adapterColonnine);
            }

            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.GREEN))
                        .addActionIcon(R.drawable.ic_map_white_24dp)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        new ItemTouchHelper(itemCallback2).attachToRecyclerView(recyclerView);
    }

    private void showSnackbar(final ArrayList<Colonnina> col) {

        final Colonnina c = col.get(0);

        StringBuilder lon = new StringBuilder(String.valueOf(c.getLongitudine()));
        StringBuilder lat = new StringBuilder(String.valueOf(c.getLatitudine()));

        lon.setCharAt(lon.indexOf("."), '-');
        lat.setCharAt(lat.indexOf("."), '-');

        String lon2 = lon.substring(0, lon.indexOf("-") + 4);
        String lat2 = lat.substring(0, lat.indexOf("-") + 4);

        final String key = lon2 + " " + lat2;


        Snackbar snackbar = Snackbar.make(linearLayout, "1 Colonnina Eliminata", Snackbar.LENGTH_LONG)
                .setAction("ANNULLA", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // cliccando su undo ricarica l'elemento eliminato sul database
                        reference.child(key).setValue(c);
                    }
                });

        snackbar.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();
        //TODO: la ricerca sarà basata su questa stringa
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
