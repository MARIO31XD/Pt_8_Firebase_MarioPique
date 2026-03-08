package com.example.firebasemario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public  class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {
    private ArrayList<Nota> listaNotas;

    private OnNotaClickListener listener; // quan fem click a una Nota


    // interface para el OnNotaClick
    public interface OnNotaClickListener {
        void onNotaClick(Nota nota);
    }

    public NotaAdapter(ArrayList<Nota> listaNotas, OnNotaClickListener listener) {
        this.listaNotas = listaNotas;
        this.listener = listener;

    }

    @NonNull
    @Override
    public  NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false);
        return new NotaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        Nota notaActual = listaNotas.get(position);
        holder.tvTitol.setText(notaActual.getTitol());
        holder.tvContingut.setText(notaActual.getContingut());

        // detecta el click a la nota, per poder eliminar-la.
        holder.itemView.setOnClickListener(v -> {
            listener.onNotaClick(notaActual);
        });
    }

    @Override
    public int getItemCount() {  // compta els items de la listaNotas
        return listaNotas.size();
    }

    public static class NotaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitol, tvContingut;

        public NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            // textView que están al xml on es mostrarà el titol i contigut de la nota: item_nota.xml
            tvTitol = itemView.findViewById(R.id.tvTitolItem);
            tvContingut = itemView.findViewById(R.id.tvContingutItem);
        }
    }

}
