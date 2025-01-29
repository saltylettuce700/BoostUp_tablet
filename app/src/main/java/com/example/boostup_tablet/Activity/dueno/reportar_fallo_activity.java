package com.example.boostup_tablet.Activity.dueno;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.R;

import java.util.ArrayList;
import java.util.List;

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



        List<String> fallosList = new ArrayList<>();
        fallosList.add("Seleccione un tipo de fallo");
        fallosList.add("Fallo eléctrico");
        fallosList.add("Fallo mecánico");
        fallosList.add("Fallo de software");
        fallosList.add("Fallo de calibración");
        fallosList.add("Otro");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, fallosList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFallo.setAdapter(adapter);

        spinnerFallo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Evitar mostrar mensaje cuando se selecciona el primer elemento (placeholder)
                if (position > 0) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    Toast.makeText(reportar_fallo_activity.this, "Seleccionaste: " + selectedItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Por dfinir xdd
            }
        });

    }
}