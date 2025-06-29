package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.Activity.dueno.home_dueno_activity;
import com.example.boostup_tablet.Activity.tecnico.home_tech_activity;
import com.example.boostup_tablet.R;

import POJO.Fallo;

public class ver_fallo_activity extends AppCompatActivity {

    TextView nombre_fallo, desc_tipo_fallo, fec_hora, txt_descripcion;
    ImageButton bt_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_fallo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bt_back = findViewById(R.id.imageButton);

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean desdeDueno = getIntent().getBooleanExtra("desde_dueno", false);
                boolean desdeTecnico = getIntent().getBooleanExtra("desde_tecnico", false);
                String token = getIntent().getStringExtra("token");

                if(desdeDueno){
                    Intent intent = new Intent(ver_fallo_activity.this, historial_fallo_activity.class);
                    intent.putExtra("tokenOwner", token);
                    intent.putExtra("desde_dueno", true);
                    intent.putExtra("desde_tecnico", false);
                    startActivity(intent);
                    finish();
                } else if (desdeTecnico) {
                    Intent intent = new Intent(ver_fallo_activity.this, historial_fallo_activity.class);
                    intent.putExtra("tokenTech", token);
                    intent.putExtra("desde_dueno", false);
                    intent.putExtra("desde_tecnico", true);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Fallo fallo = (Fallo) getIntent().getSerializableExtra("fallo");

        String nada ="Fallo no encontrado";


        nombre_fallo=findViewById(R.id.TV_nombreFallo);
        desc_tipo_fallo = findViewById(R.id.textView6);
        fec_hora = findViewById(R.id.TV_horaReporte);
        txt_descripcion = findViewById(R.id.TV_descripcion);


        if (fallo == null) {
            nombre_fallo.setText(nada);
            return;
        }

        String nomFallo = fallo.getTitulo();
        String descFallo = fallo.getTipoFalloDescripcion();
        String fechaHora = fallo.getFecha() + " " + fallo.getHora();
        String descripcion = fallo.getDescripcion();

        nombre_fallo.setText(nomFallo);
        desc_tipo_fallo.setText(descFallo);
        fec_hora.setText(fechaHora);
        txt_descripcion.setText(descripcion);


    }
}