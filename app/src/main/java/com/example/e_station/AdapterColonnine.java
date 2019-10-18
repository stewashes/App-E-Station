package com.example.e_station;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class AdapterColonnine extends RecyclerView.Adapter<AdapterColonnine.MyViewHolder> {

    private static String TAG = "AdapterColonnine";

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Colonnina> colonnine;

    public AdapterColonnine(Context context, final ArrayList<Colonnina> colonnine) {
        this.context = context;
        this.colonnine = colonnine;
        this.inflater = (LayoutInflater.from(context));
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = inflater.inflate(R.layout.item_colonnina, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    @Override

    //serve per riempire i campi del viewHolder, lo dovremmo fare con i dati del database
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.numero_posti.setText(colonnine.get(position).getN_posti());
        holder.via.setText(colonnine.get(position).getVia());
        holder.citta.setText(colonnine.get(position).getCitta());

        switch (colonnine.get(position).getMarca()) {
            case "Tesla":
                holder.logo.setImageResource(R.drawable.tesla);
                break;
            case "A2A":
                holder.logo.setImageResource(R.drawable.a2a);
                break;
            case "E-station":
                holder.logo.setImageResource(R.drawable.estation);
                break;
            case "Enel-X":
                holder.logo.setImageResource(R.drawable.enelx);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return colonnine.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView logo;

        TextView nome;
        TextView numero_posti;
        TextView via;
        TextView citta;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            logo = (ImageView) itemView.findViewById(R.id.logo_marca);
            numero_posti = (TextView) itemView.findViewById(R.id.numero);
            via = (TextView) itemView.findViewById(R.id.via);
            citta = (TextView) itemView.findViewById(R.id.citta);

        }
    }


}
