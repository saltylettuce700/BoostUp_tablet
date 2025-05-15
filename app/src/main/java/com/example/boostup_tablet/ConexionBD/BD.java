package com.example.boostup_tablet.ConexionBD;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

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
}
