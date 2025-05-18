package com.example.boostup_tablet.Activity.tecnico;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.Activity.Idle_Activity;
import com.example.boostup_tablet.Activity.inventario_activity;
import com.example.boostup_tablet.ConexionBD.BD;
import com.example.boostup_tablet.ConexionBD.Preferences;
import com.example.boostup_tablet.R;
import com.google.gson.JsonObject;

public class asignar_maquina_activity extends AppCompatActivity {
    Button bt_asignar;
    EditText et_numMaquina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asignar_maquina);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent1 = getIntent();
        String token =  intent1.getStringExtra("tokenTech");

        //Si no hay id...
        bt_asignar = findViewById(R.id.button);
        et_numMaquina = findViewById(R.id.ET_numMaquina);

        // Obtener referencias de los elementos
        ImageButton imageButton = findViewById(R.id.imageButton);
        TextView textView8 = findViewById(R.id.textView8);
        EditText etNewPassword = findViewById(R.id.ET_newpassword);

        //Checa si hay id asignado
        Preferences preferences = new Preferences(this);

        // Verificar si vino desde "home_tech_activity"
        String from = getIntent().getStringExtra("from");

        if ("home_tech".equals(from)) {

            imageButton.setVisibility(View.VISIBLE);
            textView8.setVisibility(View.VISIBLE);
            etNewPassword.setVisibility(View.VISIBLE);

            BD bd = new BD(this);
            bd.getInfoMaquina(token, new BD.JsonCallback() {
                @Override
                public void onSuccess(JsonObject obj) {
                    runOnUiThread(()->{
                        String id_maquina = obj.get("id_maquina").getAsString();
                        String ubicacion = obj.get("ubicacion").getAsString();
                        et_numMaquina.setText(id_maquina);
                        etNewPassword.setText(ubicacion);
                    });
                }

                @Override
                public void onError(String mensaje) {
                    Toast.makeText(asignar_maquina_activity.this, "Error al obtener la info", Toast.LENGTH_SHORT).show();
                }
            });

            bt_asignar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (!etNewPassword.getText().toString().isEmpty()){
                        bd.CambiarUbicacionMaquina(etNewPassword.getText().toString(), token);
                    } else {
                        Toast.makeText(asignar_maquina_activity.this, "Asigne la ubicacion", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            imageButton.setVisibility(View.GONE);
            textView8.setVisibility(View.GONE);
            etNewPassword.setVisibility(View.GONE);

            bt_asignar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (et_numMaquina.getText().toString().isEmpty()){
                        Toast.makeText(asignar_maquina_activity.this, "Favor de asignar id", Toast.LENGTH_SHORT).show();
                    }else {
                        int num_maquina = Integer.parseInt(et_numMaquina.getText().toString());
                        preferences.guardarNumMaquina(num_maquina);
                        Intent intent = new Intent(asignar_maquina_activity.this, Idle_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

        if (!"home_tech".equals(from) && preferences.obtenerNumMaquina() != 0) {
            startActivity(new Intent(this, Idle_Activity.class));
            Toast.makeText(this, "maquina: " + preferences.obtenerNumMaquina(), Toast.LENGTH_SHORT).show();
        }



        findViewById(R.id.imageButton).setOnClickListener(v -> {
            // BACK
            finish();
            Intent intent = new Intent(this, home_tech_activity.class);
            intent.putExtra("tokenTech", token);
            startActivity(intent);
            Toast.makeText(this, "Inventario", Toast.LENGTH_SHORT).show();
        });







    }
}