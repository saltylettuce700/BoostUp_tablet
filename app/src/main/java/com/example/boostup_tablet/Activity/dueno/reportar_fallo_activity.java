package com.example.boostup_tablet.Activity.dueno;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.ConexionBD.BD;
import com.example.boostup_tablet.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import POJO.TipoFallo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class reportar_fallo_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reportar_fallo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });


        Intent intent1 = getIntent();
        String tokenTemp = intent1.getStringExtra("tokenTech");
        if (tokenTemp == null || tokenTemp.isEmpty()) {
            tokenTemp = intent1.getStringExtra("tokenOwner");
        }
        String token = tokenTemp;



        EditText editText = findViewById(R.id.ET_descripcion);
        TextView charCount = findViewById(R.id.TV_charCount);
        Spinner spinnerFallo = findViewById(R.id.spinner_tipo_fallo);

        // Longitud maxima
        int maxLength = 500;

        // Configurar un TextWatcher
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nada lol
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Actualizar el contador de caracteres
                int currentLength = s.length();
                charCount.setText(currentLength + "/" + maxLength);
            }

            @Override
            public void afterTextChanged(Editable s) {
               //nada lol
            }



        });



        //List<String> fallosList = new ArrayList<>();
        /*fallosList.add("Seleccione un tipo de fallo");
        fallosList.add("Fallo eléctrico");
        fallosList.add("Fallo mecánico");
        fallosList.add("Fallo de software");
        fallosList.add("Fallo de calibración");
        fallosList.add("Otro");*/

        List<TipoFallo> listaTiposFallo = new ArrayList<>();
        TipoFallo tipoFalloDefault = new TipoFallo(1, "Otro");
        listaTiposFallo.add(tipoFalloDefault);

        BD bd = new BD(this);
        bd.getTiposFallos(token, new BD.JsonArrayCallback() {
            @Override
            public void onSuccess(JsonArray array) {
                for (JsonElement element : array){
                    JsonObject obj = element.getAsJsonObject();
                    int id = obj.get("id_tipo_fallo").getAsInt();
                    String nombre = obj.get("nombre_fallo").getAsString();
                    listaTiposFallo.add(new TipoFallo(id, nombre));
                }

                runOnUiThread(()-> {
                    ArrayAdapter<TipoFallo> adapter = new ArrayAdapter<>(
                            reportar_fallo_activity.this,
                            R.layout.spinner_item,
                            listaTiposFallo
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFallo.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() ->
                        Toast.makeText(reportar_fallo_activity.this, mensaje, Toast.LENGTH_SHORT).show()
                );
            }
        });

        spinnerFallo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TipoFallo seleccionado = (TipoFallo) parent.getItemAtPosition(position);
                int idSeleccionado = seleccionado.getId();

                if (position > 0) {
                    Toast.makeText(reportar_fallo_activity.this,
                            "Seleccionaste: " + seleccionado.getNombre() + " (ID: " + idSeleccionado + ")",
                            Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Selección por defecto: ID 1
                int idSeleccionado = 1;
                Toast.makeText(reportar_fallo_activity.this,
                        "Se seleccionó el ID por defecto: " + idSeleccionado,
                        Toast.LENGTH_SHORT);
            }
        });

        findViewById(R.id.button2).setOnClickListener(v -> {
            //mandar cambios
            //BD bd = new BD(this);

            TipoFallo tipoSeleccionado = (TipoFallo) spinnerFallo.getSelectedItem();
            int idFallo = tipoSeleccionado.getId();


            String descripcion = editText.getText().toString().trim();
            if (descripcion.isEmpty()) {
                Toast.makeText(this, "Ingrese una descripción del fallo", Toast.LENGTH_SHORT).show();
                return;
            }
            String fechaHoraActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            bd.agregarFalloMaquina(token, idFallo, fechaHoraActual, descripcion, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(reportar_fallo_activity.this, "Error al registrar el fallo", Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(reportar_fallo_activity.this, "Fallo registrado correctamente", Toast.LENGTH_SHORT).show();
                            // Redirigir a pantalla principal
                            Intent intent = new Intent(reportar_fallo_activity.this, home_dueno_activity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(reportar_fallo_activity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
//            finish();
//            startActivity(new Intent(this, home_dueno_activity.class));
//            Toast.makeText(this, "mandar cambios", Toast.LENGTH_SHORT).show();
        });


//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this, R.layout.spinner_item, fallosList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinnerFallo.setAdapter(adapter);
//
//        spinnerFallo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // Evitar mostrar mensaje cuando se selecciona el primer elemento (placeholder)
//                if (position > 0) {
//                    String selectedItem = parent.getItemAtPosition(position).toString();
//                    Toast.makeText(reportar_fallo_activity.this, "Seleccionaste: " + selectedItem, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Por dfinir xdd
//            }
//        });

    }
}