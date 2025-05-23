package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.Activity.dueno.home_dueno_activity;
import com.example.boostup_tablet.Activity.tecnico.home_tech_activity;
import com.example.boostup_tablet.ConexionBD.BD;
import com.example.boostup_tablet.R;

public class signin_tech_Activity extends AppCompatActivity {

    ImageView btn_options;
    EditText et_user, et_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin_tech);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_options = findViewById(R.id.btn_options);

        et_user = findViewById(R.id.ET_usuarioTech);
        et_pass = findViewById(R.id.ET_Password);

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            if (et_user.getText().toString().isEmpty()){
                Toast.makeText(this, "Ingresar Usuario", Toast.LENGTH_SHORT).show();
            } else if (et_pass.getText().toString().isEmpty()) {
                Toast.makeText(this, "Ingresar Contraseña", Toast.LENGTH_SHORT).show();
            } else {
                String userTech = et_user.getText().toString();
                String passTech = et_pass.getText().toString();

                BD bd = new BD(this);
                    bd.iniciarSesionTecnico(userTech, passTech, new BD.LoginCallback() {
                    @Override
                    public void onLoginSuccess(String token) {
                        Intent intent = new Intent(signin_tech_Activity.this, home_tech_activity.class);
                        intent.putExtra("tokenTech", token);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onLoginFailed() {
                        Toast.makeText(signin_tech_Activity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            /*startActivity(new Intent(this, home_tech_activity.class));
            Toast.makeText(this, "IR A PERFIL", Toast.LENGTH_SHORT).show();*/
        });

        btn_options.setOnClickListener(v -> {


            // Crear un PopupMenu
            PopupMenu popupMenu = new PopupMenu(signin_tech_Activity.this, btn_options);

            // Inflar el menú
            getMenuInflater().inflate(R.menu.options, popupMenu.getMenu());

            // Manejar las opciones seleccionadas en el menú
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.option1) {
                    // Redirigir a la actividad de Dueño
                    Intent intent = new Intent(signin_tech_Activity.this, signin_dueno_Activity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.option2) {
                    // Redirigir a la actividad de Técnico
                    Intent intent = new Intent(signin_tech_Activity.this, signin_tech_Activity.class);
                    //startActivity(intent);
                    return true;
                }
                return false;
            });


            // Mostrar el PopupMenu
            popupMenu.show();
        });
    }
}