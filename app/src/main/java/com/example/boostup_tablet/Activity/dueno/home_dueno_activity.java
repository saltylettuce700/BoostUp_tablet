package com.example.boostup_tablet.Activity.dueno;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.Activity.Idle_Activity;
import com.example.boostup_tablet.Activity.historial_fallo_activity;
import com.example.boostup_tablet.Activity.inventario_activity;
import com.example.boostup_tablet.Activity.reporte_ventas_activity;
import com.example.boostup_tablet.R;

public class home_dueno_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_dueno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.reporte_ventas_section).setOnClickListener(v -> {
            // Acción para reporte de ventas
            startActivity(new Intent(this, reporte_ventas_activity.class));
            Toast.makeText(this, "Reporte VEntas", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.inventario_section).setOnClickListener(v -> {
            // Acción para Inventario
            startActivity(new Intent(this, inventario_activity.class));
            Toast.makeText(this, "Inventario", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.reportar_fallo_section).setOnClickListener(v -> {
            // Acción para Reportar Fallo
            startActivity(new Intent(this, reportar_fallo_activity.class));
            Toast.makeText(this, "Reportar fallo", Toast.LENGTH_SHORT).show();

        });

        findViewById(R.id.historial_fallos_section).setOnClickListener(v -> {
            // Acción para Historial de Fallos
            startActivity(new Intent(this, historial_fallo_activity.class));
            Toast.makeText(this, "Historial de fallos", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.cerrar_sesion_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            finish();
            startActivity(new Intent(this, Idle_Activity.class));
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });
    }
}