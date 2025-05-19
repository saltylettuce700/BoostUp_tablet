package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.Adapter.FalloAdapter;
import com.example.boostup_tablet.ConexionBD.BD;
import com.example.boostup_tablet.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

        Intent intent1 = getIntent();
        String tokenTemp = intent1.getStringExtra("tokenTech");
        if (tokenTemp == null || tokenTemp.isEmpty()) {
            tokenTemp = intent1.getStringExtra("tokenOwner");
        }

        if (tokenTemp == null || tokenTemp.isEmpty()){
            tokenTemp = intent1.getStringExtra("token");
        }
        String token = tokenTemp;

        recyclerView = findViewById(R.id.RV_historial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BD bd = new BD(this);

        listaFallos = new ArrayList<>();
        adapter = new FalloAdapter(listaFallos, fallo -> {
            Intent intent = new Intent(historial_fallo_activity.this, ver_fallo_activity.class);
            intent.putExtra("token", token);
            intent.putExtra("fallo", fallo);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        bd.getFallosMaquina(token, new BD.JsonArrayCallback() {
            @Override
            public void onSuccess(JsonArray array) {
                runOnUiThread(() -> {
                    listaFallos.clear();
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject obj = array.get(i).getAsJsonObject();

                        int idFallo = obj.get("id_fallo").getAsInt();
                        String titulo = obj.get("tipo_fallo_nombre").getAsString();
                        String fechaHora = obj.get("fec_hora").getAsString();
                        String descripcion = obj.get("descripcion").getAsString();
                        String tipo_fallo_descripcion = obj.get("tipo_fallo_descripcion").getAsString();

                        // Separar fecha y hora
                        String[] partes = fechaHora.split("T");
                        String fecha = partes[0];
                        String hora = partes.length > 1 ? partes[1] : "";

                        Fallo fallo = new Fallo(titulo, fecha, hora, descripcion, tipo_fallo_descripcion);
                        fallo.setDescripcion(descripcion);
                        fallo.setId(idFallo);

                        listaFallos.add(fallo);
                    }
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(()-> {
                    Toast.makeText(historial_fallo_activity.this, "Error al pedir la información", Toast.LENGTH_SHORT).show();
                });
            }
        });

//        listaFallos.add(new Fallo("Fallo - Conexión con la Base de Datos", "jueves, 5 de diciembre de 2024", "9:44 p.m. - Zona horaria de Guadalajara, Jal. (GMT-6)"));
//        listaFallos.add(new Fallo("Error en Servidor", "viernes, 6 de diciembre de 2024", "10:30 a.m. - GMT-6"));
//        listaFallos.add(new Fallo("Fallo de Red", "sábado, 7 de diciembre de 2024", "3:15 p.m. - GMT-6"));
//        listaFallos.add(new Fallo("Problema en Autenticación", "domingo, 8 de diciembre de 2024", "8:00 p.m. - GMT-6"));
//        listaFallos.add(new Fallo("Tiempo de Respuesta Alto", "lunes, 9 de diciembre de 2024", "12:20 p.m. - GMT-6"));

//        adapter = new FalloAdapter(listaFallos, fallo -> {
//            // Aquí manejar el clic en "Ver Detalles"
//        });
//        recyclerView.setAdapter(adapter);
    }
}