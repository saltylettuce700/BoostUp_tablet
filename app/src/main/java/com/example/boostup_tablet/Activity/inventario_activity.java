package com.example.boostup_tablet.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.Adapter.InventarioAdapter;
import com.example.boostup_tablet.R;

import java.util.ArrayList;
import java.util.List;

import POJO.Producto;

public class inventario_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventarioAdapter adapter;
    private List<Producto> listaProductos;

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

        recyclerView = findViewById(R.id.recyclerViewInventario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        // Cargar datos de prueba
        listaProductos = new ArrayList<>();
        listaProductos.add(new Producto("Proteína Whey", 10, "05/12/2024", R.drawable.bebida_img));
        listaProductos.add(new Producto("Creatina", 15, "10/01/2025", R.drawable.bebida_img));
        listaProductos.add(new Producto("BCAA", 5, "20/03/2025", R.drawable.bebida_img));
        listaProductos.add(new Producto("Glutamina", 8, "30/04/2025", R.drawable.bebida_img));

        adapter = new InventarioAdapter(this, listaProductos);
        recyclerView.setAdapter(adapter);

    }
}