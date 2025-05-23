package com.example.boostup_tablet.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.R;

import android.os.Handler;



public class Idle_Activity extends AppCompatActivity {

    ImageView btn_options;
    View mainLayout;

    private TextView textView;
    private final Handler handler = new Handler();
    private boolean isBoostUp = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_idle);

        btn_options = findViewById(R.id.btn_options);
        mainLayout = findViewById(R.id.main);

        btn_options.setClickable(true);

        textView = findViewById(R.id.textView);

        // Iniciar el cambio de texto cada 5 segundos
        startTextSwitching();



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainLayout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // Cambiar a otra actividad
                Intent intent = new Intent(Idle_Activity.this, leer_qr_activity.class);
                startActivity(intent);
                return true; // Evento consumido
            }
            return false;
        });

        btn_options.setOnClickListener(v -> {

            // Crear un PopupMenu
            PopupMenu popupMenu = new PopupMenu(Idle_Activity.this, btn_options);

            // Inflar el menú
            getMenuInflater().inflate(R.menu.options, popupMenu.getMenu());

            // Manejar las opciones seleccionadas en el menú
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.option1) {
                    // Redirigir a la actividad de Dueño
                    Intent intent = new Intent(Idle_Activity.this, signin_dueno_Activity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.option2) {
                    // Redirigir a la actividad de Técnico
                    Intent intent = new Intent(Idle_Activity.this, signin_tech_Activity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });


            // Mostrar el PopupMenu
            popupMenu.show();
        });
    }

    private void startTextSwitching() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateTextChange();
                handler.postDelayed(this, 5000); // Volver a ejecutar después de 5 segundos
            }
        }, 5000);
    }



    private void animateTextChange() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f);
        fadeOut.setDuration(500); // Duración de la animación de desvanecimiento

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Cambiar el texto después de desvanecerse
                if (isBoostUp) {
                    textView.setText("Presione para escanear QR");
                } else {
                    textView.setText("BoostUp");
                }
                isBoostUp = !isBoostUp;

                // Animación de aparición
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
                fadeIn.setDuration(500); // Duración de la animación de aparición
                fadeIn.start();
            }
        });

        fadeOut.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Evitar fugas de memoria
    }



}


