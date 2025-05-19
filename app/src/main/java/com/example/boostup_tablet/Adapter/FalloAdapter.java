package com.example.boostup_tablet.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.Activity.dueno.home_dueno_activity;
import com.example.boostup_tablet.Activity.ver_fallo_activity;
import com.example.boostup_tablet.R;

import java.util.List;

import POJO.Fallo;

public class FalloAdapter extends RecyclerView.Adapter<FalloAdapter.FalloViewHolder>{

    private List<Fallo> listaFallos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onVerDetallesClick(Fallo fallo);
    }

    public FalloAdapter(List<Fallo> listaFallos, OnItemClickListener listener) {
        this.listaFallos = listaFallos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FalloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fallo, parent, false);
        return new FalloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FalloViewHolder holder, int position) {
        Fallo fallo = listaFallos.get(position);
        holder.tvTitulo.setText(fallo.getTitulo());
        holder.tvFecha.setText("Fecha: " + fallo.getFecha());
        holder.tvHora.setText("Hora: " + fallo.getHora());

        holder.tvVerDetalles.setOnClickListener(v -> {
            listener.onVerDetallesClick(fallo);
        });


    }

    @Override
    public int getItemCount() {
        return listaFallos.size();
    }

    public static class FalloViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvHora, tvVerDetalles, tvEstado;

        public FalloViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo_fallo);
            tvFecha = itemView.findViewById(R.id.tv_fecha_fallo);
            tvHora = itemView.findViewById(R.id.tv_hora_fallo);
            tvVerDetalles = itemView.findViewById(R.id.tv_ver_detalles);
        }
    }

}
