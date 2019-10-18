package com.example.e_station;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static String TAG = "MapsFragment";

    GoogleMap mMap;
    MapView mapView;
    View view;

    private Location location;


    private float DEFAULT_ZOOM = 13f;
    private float ZOOM_POSITION = 17f;
    private EditText ricerca;
    private ImageView gps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        ricerca = (EditText) view.findViewById(R.id.ricerca);
        gps = (ImageView) view.findViewById(R.id.gps);

        init();

        return view;
    }

    private void init() {

        Log.i(TAG, "init");

        ricerca.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute il nostro metodo per la ricerca
                    geoLocate();

                }
                return false;
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "cliccato su GPS");
                //TODO: centrare la mappa sulla posizione attuale del device !

                MainActivity m = (MainActivity) getActivity();
                location = m.getLocation();
                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());

                moveCamera(coordinate, ZOOM_POSITION);

            }
        });
    }

    private void geoLocate() {

        Log.i(TAG, "geoLocate");
        String stringaRicerca = ricerca.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(stringaRicerca, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOExecption" + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.i(TAG, "Trovata un risultato: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM);

        }

    }

    public void moveCamera(LatLng coordinate, float zoom) {
        // muovo la camera di google maps sul punto deciso dalla ricerca
        // uso animateCamera per non far muovere la mappa a scatti
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, zoom));
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated");

        mapView = (MapView) view.findViewById(R.id.mapView);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i(TAG, "onMapReady");

        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    public void placeMarker(String title, double lat, double lon, String n_posti) {

        if (mMap != null) {
            LatLng coordinate = new LatLng(lat, lon);
            // aggiunta del marker sulla mappa

            switch (title) {
                case "Tesla":
                    MarkerOptions marker = new MarkerOptions()
                            .title(title)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .snippet("Posti auto: " + n_posti)
                            .position(coordinate);
                    mMap.addMarker(marker);
                    break;
                case "A2A":
                    MarkerOptions marker2 = new MarkerOptions()
                            .title(title)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .snippet("Posti auto: " + n_posti)
                            .position(coordinate);
                    mMap.addMarker(marker2);
                    break;
                case "E-station":
                    MarkerOptions marker3 = new MarkerOptions()
                            .title(title)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .snippet("Posti auto: " + n_posti)
                            .position(coordinate);
                    mMap.addMarker(marker3);
                    break;
                case "Enel-X":
                    MarkerOptions marker4 = new MarkerOptions()
                            .title(title)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                            .snippet("Posti auto: " + n_posti)
                            .position(coordinate);
                    mMap.addMarker(marker4);
                    break;

            }

            Log.i(TAG, "Marker aggiunto");
        }
    }

    public void cancellaMarker() {
        Log.i(TAG, "Marker cancellati");
        // Elimina tutti i marker presenti sulla mappa
        mMap.clear();
    }


    public void setMyPosition() {

        MainActivity m = (MainActivity) getActivity();

        location = m.getLocation();

        LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());

        moveCamera(coordinate, ZOOM_POSITION);

    }
}
