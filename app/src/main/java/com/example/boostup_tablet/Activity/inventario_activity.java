package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.Activity.dueno.home_dueno_activity;
import com.example.boostup_tablet.Activity.tecnico.home_tech_activity;
import com.example.boostup_tablet.Adapter.InventarioAdapter;
import com.example.boostup_tablet.ConexionBD.BD;
import com.example.boostup_tablet.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import POJO.Producto;

public class inventario_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventarioAdapter adapter;
    private List<Producto> listaProductos;
    private BD bd;
    TextView txtFecPrev, txtFecLimite;
    ImageButton bt_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtFecPrev = findViewById(R.id.txt_fec_preventiva);
        txtFecLimite = findViewById(R.id.txt_fec_limite);
        bt_back = findViewById(R.id.imageButton);



        Intent intent1 = getIntent();
        String tokenTemp = intent1.getStringExtra("tokenTech");
        if (tokenTemp == null || tokenTemp.isEmpty()) {
            tokenTemp = intent1.getStringExtra("tokenOwner");
        }
        String token = tokenTemp;


        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean desdeDueno = getIntent().getBooleanExtra("desde_dueno", false);
                boolean desdeTecnico = getIntent().getBooleanExtra("desde_tecnico", false);
                if(desdeDueno){
                    Intent intent = new Intent(inventario_activity.this, home_dueno_activity.class);
                    intent.putExtra("tokenOwner", token);
                    startActivity(intent);
                    finish();
                } else if (desdeTecnico) {
                    Intent intent = new Intent(inventario_activity.this, home_tech_activity.class);
                    intent.putExtra("tokenTech", token);
                    startActivity(intent);
                    finish();
                }
            }
        });

        boolean desdeDueno = getIntent().getBooleanExtra("desde_dueno", false);

        //Toast.makeText(inventario_activity.this, "DESDE DUENO" + desdeDueno, Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recyclerViewInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bd = new BD(this);

        bd.getFechasReabastecimiento(token, new BD.JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {

                runOnUiThread(()->{
                    String fecLimite = "Fecha limite de Reabastecimiento: " + obj.get("fec_limite_abasto").getAsString();
                    String fecPrev = "Fecha Recomendada de Reabastecimiento: " + obj.get("fec_prev_abasto").getAsString();

                    txtFecLimite.setText(fecLimite);
                    txtFecPrev.setText(fecPrev);
                });


            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(()->{
                    Toast.makeText(inventario_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Cargar datos de prueba
        listaProductos = new ArrayList<>();
        bd.getInventarioMaquina(token, new BD.InventarioCallback() {
            @Override
            public void onSuccess(JsonObject respuesta) {
                runOnUiThread(() -> {
                    try {
                        listaProductos.clear();

                        if (respuesta.has("Proteina")) {
                            JsonArray proteinas = respuesta.getAsJsonArray("Proteina");
                            for (JsonElement elemento : proteinas) {
                                JsonObject obj = elemento.getAsJsonObject();
                                int id = obj.get("proteina").getAsInt();
                                int id_inv = obj.get("id_inv_proteina").getAsInt();
                                int cantidad = obj.get("cantidad_gr").getAsInt();
                                String caducidad = obj.get("fec_caducidad").getAsString();

                                bd.getDetallesProteina(id, new BD.JsonCallback() {
                                    @Override
                                    public void onSuccess(JsonObject detalles) {
                                        String nombre = "Proteína " + detalles.get("nombre").getAsString();
                                        Producto producto = new Producto(id, id_inv, "proteina", nombre, cantidad, caducidad, R.drawable.bebida_img);
                                        runOnUiThread(() -> {
                                            listaProductos.add(producto);
                                            adapter.notifyDataSetChanged();
                                        });
                                    }
                                    @Override
                                    public void onError(String error) {
                                        // Si hay error, aún así lo agregamos por ID
                                        Producto producto = new Producto(id, id_inv, "proteina", "Proteína #" + id, cantidad, caducidad, R.drawable.bebida_img);
                                        runOnUiThread(() -> {
                                            listaProductos.add(producto);
                                            adapter.notifyDataSetChanged();
                                        });
                                    }
                                });
                            }
                        }

                        if (respuesta.has("Saborizante")) {
                            JsonArray saborizantes = respuesta.getAsJsonArray("Saborizante");
                            for (JsonElement elemento : saborizantes) {
                                JsonObject obj = elemento.getAsJsonObject();
                                int id = obj.get("saborizante").getAsInt();
                                int id_inv = obj.get("id_inv_sabor").getAsInt();
                                int cantidad = obj.get("cantidad_ml").getAsInt();
                                String caducidad = obj.get("fec_caducidad").getAsString();

                                bd.getDetallesSaborizante(id, new BD.JsonCallback() {
                                    @Override
                                    public void onSuccess(JsonObject detalles) {
                                        String nombre = "Saborizante " + detalles.get("marca").getAsString() + " " + detalles.get("sabor").getAsString();
                                        Producto producto = new Producto(id, id_inv, "saborizante", nombre, cantidad, caducidad, R.drawable.bebida_img);
                                        runOnUiThread(() -> {
                                            listaProductos.add(producto);
                                            adapter.notifyDataSetChanged();
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Producto producto = new Producto(id, id_inv, "saborizante", "Saborizante #" + id, cantidad, caducidad, R.drawable.bebida_img);
                                        runOnUiThread(() -> {
                                            listaProductos.add(producto);
                                            adapter.notifyDataSetChanged();
                                        });
                                    }
                                });
                            }
                        }

                        if (respuesta.has("Curcuma")) {
                            JsonArray curcumas = respuesta.getAsJsonArray("Curcuma");
                            for (JsonElement elemento : curcumas) {
                                JsonObject obj = elemento.getAsJsonObject();
                                int id = obj.get("curcuma").getAsInt();
                                int id_inv = obj.get("id_inv_curcuma").getAsInt();
                                int cantidad = (int)(obj.get("cantidad_gr").getAsFloat()/16.5);
                                String caducidad = obj.get("fec_caducidad").getAsString();

                                bd.getDetallesCurcuma(id, new BD.JsonCallback() {
                                    @Override
                                    public void onSuccess(JsonObject detalles) {
                                        String nombre = "Cúrcuma " + detalles.get("marca").getAsString();
                                        Producto producto = new Producto(id, id_inv, "curcuma", nombre, cantidad, caducidad, R.drawable.bebida_img);
                                        runOnUiThread(() -> {
                                            listaProductos.add(producto);
                                            adapter.notifyDataSetChanged();
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Producto producto = new Producto(id, id_inv, "curcuma", "Cúrcuma #" + id, cantidad, caducidad, R.drawable.bebida_img);
                                        runOnUiThread(() -> {
                                            listaProductos.add(producto);
                                            adapter.notifyDataSetChanged();
                                        });
                                    }
                                });
                            }
                        }

                        // Cargar los datos en el RecyclerView

                        adapter = new InventarioAdapter(inventario_activity.this, listaProductos, token, desdeDueno);
                        recyclerView.setAdapter(adapter);

                    } catch (Exception e) {
                        Toast.makeText(inventario_activity.this, "Error al procesar inventario", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(inventario_activity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });






    }


}