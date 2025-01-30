package com.example.boostup_tablet.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.Adapter.FalloAdapter;
import com.example.boostup_tablet.R;

import java.util.ArrayList;
import java.util.List;

import POJO.Fallo;

public class historial_fallo_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FalloAdapter adapter;
    private List<Fallo> listaFallos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial_fallo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.RV_historial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaFallos = new ArrayList<>();
        listaFallos.add(new Fallo("Fallo - Conexión con la Base de Datos", "jueves, 5 de diciembre de 2024", "9:44 p.m. - Zona horaria de Guadalajara, Jal. (GMT-6)"));
        listaFallos.add(new Fallo("Error en Servidor", "viernes, 6 de diciembre de 2024", "10:30 a.m. - GMT-6"));
        listaFallos.add(new Fallo("Fallo de Red", "sábado, 7 de diciembre de 2024", "3:15 p.m. - GMT-6"));
        listaFallos.add(new Fallo("Problema en Autenticación", "domingo, 8 de diciembre de 2024", "8:00 p.m. - GMT-6"));
        listaFallos.add(new Fallo("Tiempo de Respuesta Alto", "lunes, 9 de diciembre de 2024", "12:20 p.m. - GMT-6"));

        adapter = new FalloAdapter(listaFallos, fallo -> {
            // Aquí manejar el clic en "Ver Detalles"
        });

        recyclerView.setAdapter(adapter);
    }
}