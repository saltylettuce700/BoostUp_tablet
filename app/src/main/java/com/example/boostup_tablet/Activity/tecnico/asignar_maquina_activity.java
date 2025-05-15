package com.example.boostup_tablet.Activity.tecnico;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.Activity.Idle_Activity;
import com.example.boostup_tablet.ConexionBD.Preferences;
import com.example.boostup_tablet.R;

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

        //Checa si hay id asignado
        Preferences preferences = new Preferences(this);

        if (preferences.obtenerNumMaquina()!= 0){
            startActivity(new Intent(this, Idle_Activity.class));
            Toast.makeText(this, "maquina: " + preferences.obtenerNumMaquina(), Toast.LENGTH_SHORT).show();
        }

        //Si no hay id...
        bt_asignar = findViewById(R.id.button);
        et_numMaquina = findViewById(R.id.ET_numMaquina);

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
}