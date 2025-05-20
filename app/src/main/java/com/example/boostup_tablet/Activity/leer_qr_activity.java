package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.ConexionBD.BD;
import com.example.boostup_tablet.R;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.os.Handler;


public class leer_qr_activity extends AppCompatActivity {

    View mainLayout;
    TextView textView;
    private Handler handler;
    private Runnable runnable;


    ImageView btn_options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        handler = new Handler();
        runnable = () -> {
            Intent intent = new Intent(leer_qr_activity.this, Idle_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        };

        handler.postDelayed(runnable, 30000);


        setContentView(R.layout.activity_leer_qr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mainLayout = findViewById(R.id.main);
        textView = findViewById(R.id.textView);

        btn_options = findViewById(R.id.btn_options);


        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(leer_qr_activity.this);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("Scan a QR Code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setCameraId(1); // Usa la cámara frontal
                intentIntegrator.initiateScan();
            }
        });

        btn_options.setOnClickListener(v -> {
            // Muestra el Toast de depuración
            Toast.makeText(leer_qr_activity.this, "Botón de opciones clickeado", Toast.LENGTH_SHORT).show();

            // Crear un PopupMenu
            PopupMenu popupMenu = new PopupMenu(leer_qr_activity.this, btn_options);

            // Inflar el menú
            getMenuInflater().inflate(R.menu.options, popupMenu.getMenu());

            // Manejar las opciones seleccionadas en el menú
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.option1) {
                    // Redirigir a la actividad de Dueño
                    Intent intent = new Intent(leer_qr_activity.this, signin_dueno_Activity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.option2) {
                    // Redirigir a la actividad de Técnico
                    Intent intent = new Intent(leer_qr_activity.this, signin_tech_Activity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });


            // Mostrar el PopupMenu
            popupMenu.show();
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            String id_pedido = intentResult.getContents();
            if (id_pedido !=null){
                BD bd = new BD(this);

                bd.getDetallesPedido(id_pedido, new BD.JsonCallback() {
                    @Override
                    public void onSuccess(JsonObject obj) {
                        boolean esConsumible = obj.get("es_consumible").getAsBoolean();
                        if (esConsumible){
                            bd.VerificarInventario(id_pedido, new BD.BooleanCallback() {
                                @Override
                                public void onSuccess(boolean preparable) {
                                    if (preparable) {
                                        //Ir a resumen_pedido
                                        handler.removeCallbacks(runnable);
                                        Intent intent = new Intent(leer_qr_activity.this, resumen_pedido_activity.class);
                                        intent.putExtra("id_pedido", id_pedido);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        //Ir a no_ingrediente_activity
                                        handler.removeCallbacks(runnable);
                                        Intent intent = new Intent(leer_qr_activity.this, no_ingrediente_activity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure() {
                                    runOnUiThread(() -> Toast.makeText(leer_qr_activity.this, "Error al verificar inventario", Toast.LENGTH_SHORT).show());
                                }
                            });
                        } else {
                            // No es consumible
                            runOnUiThread(() -> textView.setText("Solo 1 pedido cada 24 horas"));
                        }
                    }

                    @Override
                    public void onError(String mensaje) {

                    }
                });
            }
            /*String contents = intentResult.getContents();
            if(contents != null){
                textView.setText(intentResult.getContents());
                //intentResult es lo que lee el qr pero hay que pasarlo a string*/

        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}