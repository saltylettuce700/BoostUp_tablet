package com.example.boostup_tablet.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.R;

public class ver_fallo_activity extends AppCompatActivity {

    private Switch switchEstado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_fallo);

        switchEstado = findViewById(R.id.switchEstado);

        // Configurar el listener del switch
        switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchEstado.setText("Arreglado");
                switchEstado.setTextColor(Color.GREEN);
            } else {
                switchEstado.setText("Pendiente");
                switchEstado.setTextColor(Color.RED);
            }
        });

        // Asegurar que el color inicial es correcto
        if (switchEstado.isChecked()) {
            switchEstado.setTextColor(Color.GREEN);
        } else {
            switchEstado.setTextColor(Color.RED);
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}