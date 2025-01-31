package com.example.boostup_tablet.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.Adapter.ReporteAdapter;
import com.example.boostup_tablet.R;

import java.util.ArrayList;
import java.util.List;

import POJO.ReporteVenta;

public class reporte_ventas_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReporteAdapter adapter;
    private List<ReporteVenta> listaReportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reporte_ventas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crear datos de ejemplo
        listaReportes = new ArrayList<>();
        listaReportes.add(new ReporteVenta("Reporte de venta Mensual - Enero 2024", "$10,000", "150 pedidos"));
        listaReportes.add(new ReporteVenta("Reporte de venta Mensual - Febrero 2024", "$8,500", "120 pedidos"));
        listaReportes.add(new ReporteVenta("Reporte de venta Mensual - Marzo 2024", "$9,200", "130 pedidos"));

        adapter = new ReporteAdapter(listaReportes);
        recyclerView.setAdapter(adapter);
    }
}