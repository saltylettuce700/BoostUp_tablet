package com.example.boostup_tablet.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.R;

import java.util.List;

import POJO.ReporteVenta;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder> {

    private List<ReporteVenta> listaReportes;

    public ReporteAdapter(List<ReporteVenta> listaReportes) {
        this.listaReportes = listaReportes;
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        ReporteVenta reporte = listaReportes.get(position);
        holder.tvTitulo.setText(reporte.getTitulo());
        holder.tvGananciasMes.setText("Ganancias del mes: " + reporte.getGanancias());
        holder.tvCantidadPedidosMes.setText("Cantidad de pedidos del mes: " + reporte.getPedidos());
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public static class ReporteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvGananciasMes, tvCantidadPedidosMes;

        public ReporteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvGananciasMes = itemView.findViewById(R.id.tvGananciasMes);
            tvCantidadPedidosMes = itemView.findViewById(R.id.tvCantidadPedidosMes);
        }
    }

}
