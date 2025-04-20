package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.Activity.dueno.home_dueno_activity;
import com.example.boostup_tablet.R;

public class signin_dueno_Activity extends AppCompatActivity {

    ImageView btn_options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin_dueno);

        btn_options = findViewById(R.id.btn_options);
        btn_options.setClickable(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            startActivity(new Intent(this, home_dueno_activity.class));
            Toast.makeText(this, "IR A PERFIL", Toast.LENGTH_SHORT).show();
        });

        btn_options.setOnClickListener(v -> {
            // Muestra el Toast de depuración
            Toast.makeText(signin_dueno_Activity.this, "Botón de opciones clickeado", Toast.LENGTH_SHORT).show();

            // Crear un PopupMenu
            PopupMenu popupMenu = new PopupMenu(signin_dueno_Activity.this, btn_options);

            // Inflar el menú
            getMenuInflater().inflate(R.menu.options, popupMenu.getMenu());

            // Manejar las opciones seleccionadas en el menú
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.option1) {
                    // Redirigir a la actividad de Dueño
                    //Intent intent = new Intent(signin_dueno_Activity.this, signin_dueno_Activity.class);
                    //startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.option2) {
                    // Redirigir a la actividad de Técnico
                    Intent intent = new Intent(signin_dueno_Activity.this, signin_tech_Activity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });


            // Mostrar el PopupMenu
            popupMenu.show();
        });

    }
}