package com.example.boostup_tablet.Activity.dueno;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
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
import com.example.boostup_tablet.ConexionBD.BD;
import com.example.boostup_tablet.R;
import com.google.gson.JsonObject;

public class home_dueno_activity extends AppCompatActivity {

    TextView txt_username;

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

        Intent intent1 = getIntent();
        String token = intent1.getStringExtra("tokenOwner");

        txt_username = findViewById(R.id. textView4);

        BD bd = new BD(this);

        bd.getInfoOwner(token, new BD.JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {
                runOnUiThread(()->{
                    String username = obj.get("username").getAsString();

                    txt_username.setText(username);
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(home_dueno_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });

        findViewById(R.id.reporte_ventas_section).setOnClickListener(v -> {
            // Acción para reporte de ventas
            Intent intent = new Intent(this, reporte_ventas_activity.class);
            intent.putExtra("tokenOwner", token);
            startActivity(intent);
        });

        findViewById(R.id.inventario_section).setOnClickListener(v -> {
            // Acción para Inventario
            Intent intent = new Intent(this, inventario_activity.class);
            intent.putExtra("tokenOwner", token);
            intent.putExtra("desde_dueno", true);
            intent.putExtra("desde_tecnico",false);
            startActivity(intent);
        });

        findViewById(R.id.reportar_fallo_section).setOnClickListener(v -> {
            // Acción para Reportar Fallo
            Intent intent = new Intent(this, reportar_fallo_activity.class);
            intent.putExtra("tokenOwner", token);
            intent.putExtra("desde_dueno", true);
            intent.putExtra("desde_tecnico",false);
            startActivity(intent);

        });

        findViewById(R.id.historial_fallos_section).setOnClickListener(v -> {
            // Acción para Historial de Fallos
            Intent intent = new Intent(this, historial_fallo_activity.class);
            intent.putExtra("tokenOwner", token);
            intent.putExtra("desde_dueno", true);
            intent.putExtra("desde_tecnico",false);
            startActivity(intent);
        });

        findViewById(R.id.cerrar_sesion_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            finish();
            startActivity(new Intent(this, Idle_Activity.class));
        });
    }
}