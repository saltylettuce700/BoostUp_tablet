package com.example.boostup_tablet.ConexionBD;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final  String NOMBRE_PREF = "AuthPrefs";
    private SharedPreferences prefs;

    public Preferences(Context context){
        prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
    }

    public void guardarNumMaquina ( int id_maquina){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("id_maquina", id_maquina);
        editor.apply();
    }

    public int obtenerNumMaquina(){
        return prefs.getInt("id_maquina", 0);
    }

    public void eliminarNumMaquina(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("id_maquina");
        editor.apply();
    }
}
