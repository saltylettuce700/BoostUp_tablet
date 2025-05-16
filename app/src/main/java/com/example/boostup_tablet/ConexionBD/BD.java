package com.example.boostup_tablet.ConexionBD;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.boostup_tablet.Activity.dueno.home_dueno_activity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

    /*------------------Autentificaciones-------------------------------*/

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailed();
    }

    public void iniciarSesionOwner (String user, String pass, LoginCallback callback){

        getAuthTokenOwner(user, pass, new AuthCallback() {
            @Override
            public void onSuccess(String token) {
                runOnUiThread(() -> {
                    // Mostrar mensaje
                    Toast.makeText(context, "Sesion dueño iniciada", Toast.LENGTH_SHORT).show();
                    callback.onLoginSuccess();
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
                    callback.onLoginSuccess();
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

}
