package com.example.boostup_tablet.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.R;

public class Idle_Activity extends AppCompatActivity {

    ImageView btn_options;
    View mainLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_idle);

        btn_options = findViewById(R.id.btn_options);
        mainLayout = findViewById(R.id.main);

        btn_options.setClickable(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainLayout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // Cambiar a otra actividad
                Intent intent = new Intent(Idle_Activity.this, signin_dueno_Activity.class);
                startActivity(intent);
                return true; // Evento consumido
            }
            return false;
        });

        btn_options.setOnClickListener(v -> {
            // Muestra el Toast de depuración
            Toast.makeText(Idle_Activity.this, "Botón de opciones clickeado", Toast.LENGTH_SHORT).show();

            // Crear un PopupMenu
            PopupMenu popupMenu = new PopupMenu(Idle_Activity.this, btn_options);

            // Inflar el menú
            getMenuInflater().inflate(R.menu.options, popupMenu.getMenu());


            // Mostrar el PopupMenu
            popupMenu.show();
        });
    }
}
