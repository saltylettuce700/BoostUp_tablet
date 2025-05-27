package com.example.boostup_tablet.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class Idle_Activity extends AppCompatActivity {
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


    ImageView btn_options;
    View mainLayout;

    private TextView textView;
    private final Handler handler = new Handler();
    private boolean isBoostUp = false;

    float humedadReportada = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        initResolveListener();
        initDiscoveryListener();
        startServiceDiscovery();

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
                webSocket.send("blink(1)");
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


