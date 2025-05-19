package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.ConexionBD.BD;
import com.example.boostup_tablet.R;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class resumen_pedido_activity extends AppCompatActivity {

    TextView txtBebidaUsername, txtPrecio, txtFecha, txtNombreProteina, txtCantidadProte, txtMarcaProte;
    TextView txtSabor, txtTipoSaborizante, txtMarcaSaborizante, txtMarcaCurcuma, txtCantidadCurcuma;
    Button bt_siguiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resumen_pedido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String id_pedido = intent.getStringExtra("id_pedido");

        txtBebidaUsername = findViewById(R.id.TV_bebidaUsername);
        txtPrecio = findViewById(R.id.tvProductPrice);
        txtFecha = findViewById(R.id.tvOrderDate);
        txtNombreProteina = findViewById(R.id.tvNombreProteina);
        txtCantidadProte = findViewById(R.id.tvCantidadProteina);
        txtMarcaProte = findViewById(R.id.tvMarcaProteina);
        txtSabor = findViewById(R.id.tvSabor);
        txtTipoSaborizante = findViewById(R.id.tvTipoSaborizante);
        txtMarcaSaborizante =findViewById(R.id.tvMarcaSaborizante);
        txtMarcaCurcuma = findViewById(R.id.tvMarcaCurcuma);
        txtCantidadCurcuma = findViewById(R.id.tvCantidadCurcuma);

        bt_siguiente = findViewById(R.id.button3);

        BD bd = new BD(this);

        bd.getDetallesPedido(id_pedido, new BD.JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {
                runOnUiThread(() -> {
                    try {
                        // Extraer datos desde el JSON
                        String proteina = obj.get("proteina").getAsString();
                        double monto = obj.get("monto_total").getAsDouble();
                        String fechaCompra = obj.get("fec_hora_compra").getAsString();
                        // String estadoCanje = obj.get("estado_canje").getAsString(); // Si lo quieres usar, puedes agregar otro TextView
                        int proteinaGr = obj.get("proteina_gr").getAsInt();
                        String sabor = obj.get("sabor").getAsString();
                        String tipoSabor = obj.get("tipo_saborizante").getAsString();
                        String marcaProteina = obj.get("proteina_marca").getAsString();
                        String marcaSaborizante = obj.get("saborizante_marca").getAsString();

                        String curcumaMarca = obj.has("curcuma_marca") && !obj.get("curcuma_marca").isJsonNull()
                                ? obj.get("curcuma_marca").getAsString()
                                : "N/A";

                        int curcumaGr = obj.has("curcuma_gr") && !obj.get("curcuma_gr").isJsonNull()
                                ? obj.get("curcuma_gr").getAsInt()
                                : 0;

                        // Mostrar los datos en los TextView
                        txtBebidaUsername.setText(proteina); // Aquí asumí que "proteina" es el nombre del usuario que pidió la bebida, si no ajusta.
                        txtPrecio.setText(String.format("$ %.2f", monto));
                        txtFecha.setText(fechaCompra);
                        txtNombreProteina.setText(proteina);
                        txtCantidadProte.setText(proteinaGr + " gr");
                        txtMarcaProte.setText(marcaProteina);
                        txtSabor.setText(sabor);
                        txtTipoSaborizante.setText(tipoSabor);
                        txtMarcaSaborizante.setText(marcaSaborizante);
                        txtMarcaCurcuma.setText(curcumaMarca);
                        txtCantidadCurcuma.setText(curcumaGr + " gr");

                    } catch (Exception e) {
                        Toast.makeText(resumen_pedido_activity.this, "Error al mostrar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(()->{
                    Toast.makeText(resumen_pedido_activity.this, "Error al obtener detalles, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(resumen_pedido_activity.this, Idle_Activity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    finish();
                });
            }
        });

        bt_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //humedad hardcodeada

                bd.CanjearPedido(id_pedido, 50, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(()->{
                            Toast.makeText(resumen_pedido_activity.this, "No se pudo canjear " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> {
                                Toast.makeText(resumen_pedido_activity.this, "Pedido canjeado", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(resumen_pedido_activity.this, poner_vaso_activity.class);
                                //intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent1);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(resumen_pedido_activity.this, "Error al canjear: " + response.code(), Toast.LENGTH_SHORT).show();
                            });
                        }


                    }
                });
            }
        });

    }
}