package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import android.util.Log;


public class resumen_pedido_activity extends AppCompatActivity {

    // Conexion con maquina
    private NsdManager nsdManager;
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;

    // Aqui se almacena el host y puerto de la ESP32
    private String serviceHost;
    private int servicePort;
    private WebSocket webSocket;

    private static final String TAG = "NSD"; // Para encontrar los logs facil
    private static final String SERVICE_TYPE = "_ws._tcp."; // El servicio que estamos buscando

    // Humedad reportada
    private float humedadReportada = 0;

    // Detalles de la bebida
    TextView txtBebidaUsername, txtPrecio, txtFecha, txtNombreProteina, txtCantidadProte, txtMarcaProte;
    TextView txtSabor, txtTipoSaborizante, txtMarcaSaborizante, txtMarcaCurcuma, txtCantidadCurcuma;
    Button bt_siguiente;

    ImageView img1, img2, img3;

    int idProteina = 0;
    int idSaborizante = 0;
    float gramosProteina = 0;
    float mililitrosSabor = 0;
    float curcumaGr = 0;

    float gramosCurcuma = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        initResolveListener();
        initDiscoveryListener();
        startServiceDiscovery();

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



        img1 = findViewById(R.id.imgProducto1);
        img2 = findViewById(R.id.imgProducto2);
        img3 = findViewById(R.id.imgProducto3);

        BD bd = new BD(this);

        if (humedadReportada>=80){

            bd.insertarHumedad(humedadReportada, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(()->{
                        Toast.makeText(resumen_pedido_activity.this, "Error registrando la humedad", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(()->{
                        Toast.makeText(resumen_pedido_activity.this, "Humedad registrada, avisando al tecnico", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(resumen_pedido_activity.this, mantenimiento_activity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                    });
                }
            });

        }

        bd.getDetallesPedido(id_pedido, new BD.JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {
                runOnUiThread(() -> {
                    try {
                        // Extraer datos desde el JSON
                        String proteina = obj.get("proteina").getAsString();
                        double monto = obj.get("monto_total").getAsDouble();
                        String fechaCompra = obj.get("fec_hora_compra").getAsString().split("T")[0]; // Solo la fecha, sin la hora;
                        // String estadoCanje = obj.get("estado_canje").getAsString(); // Si lo quieres usar, puedes agregar otro TextView
                        int proteinaGr = obj.get("proteina_gr").getAsInt();
                        String sabor = obj.get("sabor").getAsString();
                        String tipoSabor = obj.get("tipo_saborizante").getAsString();
                        String marcaProteina = obj.get("proteina_marca").getAsString();
                        String marcaSaborizante = obj.get("saborizante_marca").getAsString();

                        String curcumaMarca = obj.has("curcuma_marca") && !obj.get("curcuma_marca").isJsonNull()
                                ? obj.get("curcuma_marca").getAsString()
                                : "N/A";

                        if (obj.has("curcuma_marca")) {
                            gramosCurcuma = obj.get("curcuma_gr").getAsFloat();
                        }
                        idProteina = obj.get("id_proteina").getAsInt();
                        idSaborizante = obj.get("id_saborizante").getAsInt();
                        mililitrosSabor = obj.get("saborizante_ml").getAsFloat();
                        gramosProteina = obj.get("proteina_gr").getAsFloat();

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


                        /*ImageView imageBackground = findViewById(R.id.imageView2);
                        switch (sabor.toLowerCase()) {
                            case "chocolate":
                                imageBackground.setImageResource(R.drawable.choco_pedido_min);
                                break;
                            case "vainilla":
                                imageBackground.setImageResource(R.drawable.vanilla_pedido_min);
                                break;
                            case "fresa":
                                imageBackground.setImageResource(R.drawable.strawberry_pedido_min);
                                break;
                            default:
                                imageBackground.setImageResource(R.drawable.bebida_img); // Imagen por defecto
                                break;
                        }*/

                        int resID1 = obtenerImagenPorProducto("Proteínas", proteina);
                        int resID2 = obtenerImagenPorProducto("Saborizantes",sabor);
                        int resID3 = obtenerImagenPorProducto("Cúrcuma y Jengibre",curcumaMarca);

                        img1.setImageResource(resID1);
                        img2.setImageResource(resID2);
                        img3.setImageResource(resID3);

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

                bd.CanjearPedido(id_pedido, humedadReportada, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(()->{
                            Toast.makeText(resumen_pedido_activity.this, "No se pudo canjear " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            webSocket.send("blink(3)");
                            webSocket.close(1000, "Humidity purpouses finished");
                            runOnUiThread(() -> {
                                Toast.makeText(resumen_pedido_activity.this, "Pedido canjeado", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(resumen_pedido_activity.this, poner_vaso_activity.class);
                                intent1.putExtra("idProteina", idProteina );
                                intent1.putExtra("idSaborizante", idSaborizante);
                                intent1.putExtra("grProteina", gramosProteina);
                                intent1.putExtra("grCurcuma", gramosCurcuma);
                                intent1.putExtra("mlSaborizante", mililitrosSabor);
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

    private int obtenerImagenPorProducto(String categoria, String valorClave) {
        if (valorClave == null) return 0;

        valorClave = valorClave.toLowerCase();

        switch (categoria) {
            case "Proteínas":
                if (valorClave.contains("pure and natural")) return R.drawable.pure_natural_img;
                if (valorClave.contains("falcon")) return R.drawable.falcon;
                return 0;

            case "Saborizantes":
                if (valorClave.contains("fresa")||valorClave.contains("strawberry")) return R.drawable.strawberry_milk;
                if (valorClave.contains("chocolate")) return R.drawable.choco_milk;
                if (valorClave.contains("vainilla")|| valorClave.contains("vanilla")) return R.drawable.vanilla_milk;


                return 0;

            case "Cúrcuma y Jengibre":
                if (valorClave.contains("nature heart")) return R.drawable.nature_heart_turmeric;

                return R.drawable.cross_icon;
        }

        return 0;
    }

    private void initDiscoveryListener() {
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Discovery started for type: " + regType);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service found: " + serviceInfo);
                if (serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Found a matching WS service: " + serviceInfo.getServiceName());
                    nsdManager.resolveService(serviceInfo, resolveListener);
                } else {
                    Log.d(TAG, "Found different service type: " + serviceInfo.getServiceType());
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service lost: " + serviceInfo);
                // If it’s the service you’re using, consider closing WebSocket
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Start discovery failed: Error code: " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Stop discovery failed: Error code: " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };
    }

    private void initResolveListener() {
        resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service resolved: " + serviceInfo);
                String serviceHost = serviceInfo.getHost().getHostAddress();
                servicePort = serviceInfo.getPort();
                Log.d(TAG, "Resolved host: " + serviceHost + ", port: " + servicePort);

                // Now connect over WebSocket:
                startWebSocketClient(serviceHost, servicePort);
            }
        };
    }

    private void startServiceDiscovery() {
        nsdManager.discoverServices(
                SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD,
                discoveryListener);
    }

    private void startWebSocketClient(String host, int port) {
        OkHttpClient client = new OkHttpClient();
        String wsUrl = "ws://" + host + ":" + port + "/ws";
        Request request = new Request.Builder()
                .url(wsUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d(TAG, "WebSocket opened");
                webSocket.send("blink(2)");
                webSocket.send("readHumidity()");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received: " + text);

                try {
                    humedadReportada = Float.parseFloat(text);
                } catch (Exception e) {
                    Log.e("HumidityParse", e.getMessage());
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.d(TAG, "Received bytes: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.d(TAG, "Closing: " + code + " / " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "Closed: " + code + " / " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }
}