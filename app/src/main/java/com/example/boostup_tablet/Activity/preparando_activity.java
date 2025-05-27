package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.boostup_tablet.Activity.dueno.reportar_fallo_activity;
import com.example.boostup_tablet.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class preparando_activity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        initResolveListener();
        initDiscoveryListener();
        startServiceDiscovery();

        setContentView(R.layout.activity_preparando);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                webSocket.send("blink(4)");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received: " + text);

                if (text.equals("Order finished")) {
                    // Run on UI thread
                    runOnUiThread(() -> {
                        Intent intent = new Intent(preparando_activity.this, tomar_bebida_activity.class);
                        startActivity(intent);
                    });
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