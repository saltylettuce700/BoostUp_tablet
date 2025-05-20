package com.example.boostup_tablet.ConexionBD;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.boostup_tablet.Activity.dueno.home_dueno_activity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BD {
    private static final String BASE_URL = "https://boostup.life/";
    private final Gson gson;
    private final OkHttpClient client;
    private Context context;

    public BD(Context context) {
        this.context = context;
        gson = new GsonBuilder().create();
        client = new OkHttpClient();
    }

    //Funciones uso comun
    public void runOnUiThread(Runnable action) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(action);
        }
    }

    public interface JsonCallback {
        void onSuccess(JsonObject obj);
        void onError(String mensaje);
    }

    public interface JsonArrayCallback {
        void onSuccess(JsonArray array);
        void onError(String mensaje);
    }

    public interface BooleanCallback {
        void onSuccess(boolean preparable);
        void onFailure();
    }

    public interface InventarioCallback {
        void onSuccess(JsonObject inventarioCompleto);
        void onError(String mensaje);
    }


    private void getRequest(String route, Callback callback) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    private void PostRequest(String route, JSONObject json, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        final String JSONStr = json.toString();

        final RequestBody requestBody = RequestBody.create(
                MediaType.get("application/json; charset=utf-8"),
                JSONStr
        );

        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    private void authGetRequest(String token, String route, Callback callback) {

        if (token == null || token.isEmpty()) {
            callback.onFailure(null, new IOException("Token no encontrado"));
            return;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    private void putAuthRequest(String token, String route, String jsonBody, Callback callback) {

        if (token == null || token.isEmpty()) {
            callback.onFailure(null, new IOException("Token no encontrado"));
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        // Crear el cuerpo de la solicitud con el JSON
        RequestBody body = RequestBody.create(JSON, jsonBody);

        // Construir la solicitud PUT con el token de autenticación
        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .put(body)
                .build();

        // Realizar la llamada a la API
        client.newCall(request).enqueue(callback);
    }

    private void PostAuthRequest(String token, String route, JSONObject json, Callback callback) {


        if (token == null || token.isEmpty()) {
            callback.onFailure(null, new IOException("Token no encontrado"));
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + route)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    /*------------------Autentificaciones-------------------------------*/

    public interface LoginCallback {
        void onLoginSuccess(String token);
        void onLoginFailed();
    }

    public void iniciarSesionOwner (String user, String pass, LoginCallback callback){

        getAuthTokenOwner(user, pass, new AuthCallback() {
            @Override
            public void onSuccess(String token) {
                runOnUiThread(() -> {
                    // Mostrar mensaje
                    Toast.makeText(context, "Sesion dueño iniciada", Toast.LENGTH_SHORT).show();
                    callback.onLoginSuccess(token);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    callback.onLoginFailed();
                });
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(String token);
        void onFailure(String error);
    }

    public void getAuthTokenOwner(String username, String password, AuthCallback callback){
        String ruta = BASE_URL + "owner/token/";

        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "password")
                .add("username", username)
                .add("password", password)
                .add("scope", "")
                .add("client_id", "string")
                .add("client_secret", "string")
                .build();
        Request request = new Request.Builder()
                .url(ruta)
                .addHeader("accept", "application/json")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()){
                    if (!response.isSuccessful()){
                        runOnUiThread(() -> callback.onFailure("HTTP error: " + response.code()));
                        return;
                    }

                    assert  responseBody != null;
                    String responseData = responseBody.string();

                    try {
                        JSONObject json = new JSONObject(responseData);
                        String token = json.optString("access_token");
                        if (!token.isEmpty()){
                            runOnUiThread(()-> callback.onSuccess(token));
                        }else{
                            runOnUiThread(()-> callback.onFailure("Invalid Token in response"));
                        }
                    } catch (JSONException e) {
                        runOnUiThread(()-> callback.onFailure("JSON parsing error"));
                    }
                }
            }
        });
    }

    public void iniciarSesionTecnico (String user, String pass, LoginCallback callback){

        getAuthTokenTecnico(user, pass, new AuthCallback() {
            @Override
            public void onSuccess(String token) {
                runOnUiThread(() -> {
                    // Mostrar mensaje
                    Toast.makeText(context, "Sesion tecnico iniciada", Toast.LENGTH_SHORT).show();
                    callback.onLoginSuccess(token);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    callback.onLoginFailed();
                });
            }
        });
    }

    public void getAuthTokenTecnico(String username, String password, AuthCallback callback){
        String ruta = BASE_URL + "technician/token/";

        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "password")
                .add("username", username)
                .add("password", password)
                .add("scope", "")
                .add("client_id", "string")
                .add("client_secret", "string")
                .build();
        Request request = new Request.Builder()
                .url(ruta)
                .addHeader("accept", "application/json")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()){
                    if (!response.isSuccessful()){
                        runOnUiThread(() -> callback.onFailure("HTTP error: " + response.code()));
                        return;
                    }

                    assert  responseBody != null;
                    String responseData = responseBody.string();

                    try {
                        JSONObject json = new JSONObject(responseData);
                        String token = json.optString("access_token");
                        if (!token.isEmpty()){
                            runOnUiThread(()-> callback.onSuccess(token));
                        }else{
                            runOnUiThread(()-> callback.onFailure("Invalid Token in response"));
                        }
                    } catch (JSONException e) {
                        runOnUiThread(()-> callback.onFailure("JSON parsing error"));
                    }
                }
            }
        });
    }

    /*------------------GETS-------------------------------*/

    public void getInfoOwner(String token, JsonCallback callback){
        String ruta = "owner/yo/";

        authGetRequest(token, ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar los datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getInfoTech(String token, JsonCallback callback){
        String ruta = "technician/yo/";

        authGetRequest(token, ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar los datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getInfoMaquina(String token, JsonCallback callback){
        String ruta = "technician/maquina/info/";

        authGetRequest(token, ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar los datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getTiposFallos(String token, JsonArrayCallback callback){
        String ruta = "owner_or_tech/tipos-fallo/";

        authGetRequest(token, ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                        callback.onSuccess(array);
                    } catch (Exception e) {
                        callback.onError("Error al obtener los datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getFallosMaquina(String token, JsonArrayCallback callback){
        String ruta = "owner_or_tech/maquina/fallos/";

        authGetRequest(token, ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                        callback.onSuccess(array);
                    } catch (Exception e) {
                        callback.onError("Error al obtener los datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void VerificarInventario(String id_pedido, BooleanCallback callback){
        Preferences preferences = new Preferences(context);
        int num_maquina = preferences.obtenerNumMaquina();

        String ruta = "machine/inventario/"+ num_maquina + "/disponible/"+id_pedido;

        getRequest(ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String respuesta = response.body().string(); // trim para evitar espacios o saltos de línea
                    boolean preparable = Boolean.parseBoolean(respuesta);
                    callback.onSuccess(preparable);
                } else {
                    callback.onFailure();
                }
            }
        });

    }

    public void getDetallesPedido(String id, JsonCallback callback) {
        String ruta = "pedido/"+ id+"/";

        getRequest(ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show());
                callback.onError("Error de red: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Error HTTP: " + response.message());
                    return;
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    runOnUiThread(() -> Toast.makeText(context, "Pedido no encontrado", Toast.LENGTH_SHORT).show());
                    callback.onError("Pedido no encontrado");
                    return;
                }

                try {
                    String json = responseBody.string();
                    JsonObject obj = gson.fromJson(json, JsonObject.class);

                    String proteina = obj.get("proteina").getAsString();
                    double monto = obj.get("monto_total").getAsDouble();
                    String fechacompra = obj.get("fec_hora_compra").getAsString();
                    String estadocanje = obj.get("estado_canje").getAsString();
                    int proteinagr = obj.get("proteina_gr").getAsInt();
                    String sabor = obj.get("sabor").getAsString();
                    String tipoSabor = obj.get("tipo_saborizante").getAsString();
                    String marcaProteina = obj.get("proteina_marca").getAsString();
                    String marcaSaborizante = obj.get("saborizante_marca").getAsString();
                    boolean consumible = obj.get("es_consumible").getAsBoolean();

                    String curcumaMarca = obj.has("curcuma_marca") && !obj.get("curcuma_marca").isJsonNull()
                            ? obj.get("curcuma_marca").getAsString()
                            : "N/A";

                    int curcumaGr = obj.has("curcuma_gr") && !obj.get("curcuma_gr").isJsonNull()
                            ? obj.get("curcuma_gr").getAsInt()
                            : 0;

                    // Retornar los datos al callback
                    callback.onSuccess(obj);

                } catch (Exception e) {
                    callback.onError("Error al procesar JSON: " + e.getMessage());
                }
            }


        });
    }

    public void getInventarioMaquina(String token, InventarioCallback callback){
        String ruta = "owner_or_tech/inventario/";

        authGetRequest(token, ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar los datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getDetallesProteina(int id, JsonCallback callback){
        String ruta = "proteina/"+id+"/detalles/";

        getRequest(ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getDetallesSaborizante(int id, JsonCallback callback){
        String ruta = "saborizante/"+id+"/detalles";

        getRequest(ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    public void getDetallesCurcuma(int id, JsonCallback callback){
        String ruta= "curcuma/"+id+"/detalles/";

        getRequest(ruta, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                        callback.onSuccess(obj);
                    } catch (Exception e) {
                        callback.onError("Error al procesar datos");
                    }
                } else {
                    callback.onError("Error en la respuesta del servidor");
                }
            }
        });
    }

    /*------------------PUTS-------------------------------*/

    public void CambiarUbicacionMaquina(String ubi, String token){
        String ruta = "technician/maquina/ubicacion/";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ubicacion", ubi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonBody = jsonObject.toString();

        putAuthRequest(token, ruta, jsonBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->{
                    Toast.makeText(context, "Fallo: "+e, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(()->{
                    Toast.makeText(context, "Ubicación modificada", Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    /*------------------POSTS-------------------------------*/

    public void agregarFalloMaquina (String token, int tipo, String fec_hora, String descripcion, Callback callback){
        String ruta = "owner_or_tech/maquina/fallos/";

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("tipo_fallo", tipo);
            jsonObject.put("fec_hora", fec_hora);
            jsonObject.put("descripcion", descripcion);
        } catch (JSONException e) {
            runOnUiThread(()->{
                Toast.makeText(context, "Error al procesar el fallo", Toast.LENGTH_SHORT).show();
                return;
            });
        }

        PostAuthRequest(token, ruta, jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(context, "Error al registrar el fallo", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    Toast.makeText(context, "Fallo registrado", Toast.LENGTH_LONG).show();
                });
            }
        });


    }

    public void CanjearPedido(String id_pedido, float humedad, Callback callback){
        String ruta = "machine/canjearPedido/";

        Preferences preferences = new Preferences(context);
        int num_maquina = preferences.obtenerNumMaquina();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("machine_id", num_maquina);
            jsonObject.put("order_id", id_pedido);
            jsonObject.put("current_humidity", humedad);
        } catch (JSONException e) {
            runOnUiThread(()->{
                Toast.makeText(context, "Error con los datos del pedido", Toast.LENGTH_SHORT).show();

            });
            return;
        }

        PostRequest(ruta, jsonObject, callback);
    }

}
