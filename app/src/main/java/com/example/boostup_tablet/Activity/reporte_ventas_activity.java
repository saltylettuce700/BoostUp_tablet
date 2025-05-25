package com.example.boostup_tablet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.Activity.dueno.home_dueno_activity;
import com.example.boostup_tablet.Adapter.ReporteAdapter;
import com.example.boostup_tablet.ConexionBD.BD;
import com.example.boostup_tablet.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import POJO.ReporteVenta;

public class reporte_ventas_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReporteAdapter adapter;
    private List<ReporteVenta> lista = new ArrayList<>();

    private List<ReporteVenta> listaReportes = new ArrayList<>();
    private Map<String, float[]> mapaSabores = new HashMap<>(); // sabor → array de 12 meses
    private ArrayList<BarEntry> entradasBarras = new ArrayList<>();
    private int respuestasRecibidas = 0;

    BarChart ventasChart;
    LineChart saboresChart;
    ImageButton bt_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reporte_ventas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String token = intent.getStringExtra("tokenOwner");

        ventasChart = findViewById(R.id.ventas_chart);
        saboresChart = findViewById(R.id.sabores_chart);

        recyclerView = findViewById(R.id.recyclerView); // Asegúrate que este ID coincida con tu XML
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bt_back = findViewById(R.id.imageButton);

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(reporte_ventas_activity.this, home_dueno_activity.class);
                intent1.putExtra("tokenOwner", token);
                startActivity(intent1);
                finish();
            }
        });

        int anioActual = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        int mesActual = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1;

        for (int i = 0; i < 12; i++) {
            int mes = mesActual - i;
            int anio = anioActual;
            if (mes <= 0) {
                mes += 12;
                anio -= 1;
            }

            final int mesFinal = mes;   // variables finales locales
            final int anioFinal = anio;
            final int index = 11 - i; // para que queden ordenados de enero a diciembre

            BD bd = new BD(this);
            bd.reporteMensual(token, anioFinal, mesFinal, new BD.reporteVentas() {
                @Override
                public void onSuccess(JsonObject reporte) {
                    procesarReporteMensual(reporte, anioFinal, mesFinal, index);
                }

                @Override
                public void onError(String mensaje) {
                    runOnUiThread(() -> {
                        respuestasRecibidas++;
                        verificarFinalizacion();
                    });
                }
            });
        }



//        // Crear datos de ejemplo
////        listaReportes = new ArrayList<>();
////        listaReportes.add(new ReporteVenta("Reporte de venta Mensual - Enero 2024", "$10,000", "150 pedidos"));
////        listaReportes.add(new ReporteVenta("Reporte de venta Mensual - Febrero 2024", "$8,500", "120 pedidos"));
////        listaReportes.add(new ReporteVenta("Reporte de venta Mensual - Marzo 2024", "$9,200", "130 pedidos"));
//
////        adapter = new ReporteAdapter(listaReportes);
////        recyclerView.setAdapter(adapter);
//
//        //Datos de la grafica
//
//        ArrayList<BarEntry> barEntries = new ArrayList<>();
//
//        for (int i = 1; i <13; i++){
//            //convert to float
//
//            float value = (float) (i*13.0);
//            //initialize bar chart entry
//
//            BarEntry barEntry = new BarEntry(i,value);
//            barEntries.add(barEntry);
//        }
//
//        //initialize bar data set
//
//        BarDataSet barDataSet = new BarDataSet(barEntries, "Meses del año");
//        //set color
//
//        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
//        //set bar data
//        ventasChart.setData(new BarData(barDataSet));
//
//        //Set animation
//
//        ventasChart.animateY(5000);
//
//        ventasChart.getDescription().setText("Ventas anuales");
//
//        //grafica de lineas
//
//        // Datos simulados por sabor para cada mes (enero = 0, diciembre = 11)
//        float[] pedidosFresa = {12, 18, 15, 20, 25, 30, 28, 27, 24, 22, 19, 21};
//        float[] pedidosChocolate = {10, 14, 13, 17, 23, 25, 29, 26, 20, 18, 16, 19};
//        float[] pedidosVainilla = {8, 12, 11, 14, 18, 21, 20, 22, 19, 15, 13, 17};
//
//        ArrayList<Entry> entriesFresa = new ArrayList<>();
//        ArrayList<Entry> entriesChocolate = new ArrayList<>();
//        ArrayList<Entry> entriesVainilla = new ArrayList<>();
//
//        for (int i = 0; i < 12; i++) {
//            entriesFresa.add(new Entry(i, pedidosFresa[i]));
//            entriesChocolate.add(new Entry(i, pedidosChocolate[i]));
//            entriesVainilla.add(new Entry(i, pedidosVainilla[i]));
//        }
//
//        LineDataSet dataSetFresa = new LineDataSet(entriesFresa, "Fresa");
//        dataSetFresa.setColor(ColorTemplate.COLORFUL_COLORS[0]);
//        dataSetFresa.setCircleColor(ColorTemplate.COLORFUL_COLORS[0]);
//
//        LineDataSet dataSetChocolate = new LineDataSet(entriesChocolate, "Chocolate");
//        dataSetChocolate.setColor(ColorTemplate.COLORFUL_COLORS[1]);
//        dataSetChocolate.setCircleColor(ColorTemplate.COLORFUL_COLORS[1]);
//
//        LineDataSet dataSetVainilla = new LineDataSet(entriesVainilla, "Vainilla");
//        dataSetVainilla.setColor(ColorTemplate.COLORFUL_COLORS[2]);
//        dataSetVainilla.setCircleColor(ColorTemplate.COLORFUL_COLORS[2]);
//
//        LineData lineData = new LineData(dataSetFresa, dataSetChocolate, dataSetVainilla);
//
//        saboresChart.setData(lineData);
//
//        // Configurar ejes
//        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
//
//        saboresChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                int index = (int) value;
//                return (index >= 0 && index < meses.length) ? meses[index] : "";
//            }
//        });
//
//        saboresChart.getXAxis().setGranularity(1f);
//        saboresChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//
//        saboresChart.getDescription().setText("Pedidos por sabor");
//        saboresChart.animateX(2000);
//        saboresChart.invalidate(); // refrescar
//
//        dataSetFresa.setDrawValues(true);
//        dataSetChocolate.setDrawValues(true);
//        dataSetVainilla.setDrawValues(true);
//
//        dataSetFresa.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        dataSetChocolate.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        dataSetVainilla.setMode(LineDataSet.Mode.CUBIC_BEZIER);


    }



    private void procesarReporteMensual(JsonObject reporte, int anio, int mes, int index) {
        runOnUiThread(() -> {
            JsonObject total = reporte.getAsJsonObject("total");
            String ganancias = "$" + total.get("ganancias").getAsString();
            String pedidos = total.get("cantidad").getAsString() + " pedidos";
            String titulo = "Reporte de venta Mensual - " + obtenerNombreMes(mes) + " " + anio;

            listaReportes.add(new ReporteVenta(titulo, ganancias, pedidos));
            entradasBarras.add(new BarEntry(index, total.get("cantidad").getAsFloat()));

            for (Map.Entry<String, JsonElement> entry : reporte.entrySet()) {
                if (!entry.getKey().equals("total")) {
                    JsonObject saborObj = entry.getValue().getAsJsonObject();
                    String sabor = saborObj.get("sabor").getAsString();
                    int cantidad = saborObj.get("pedidos").getAsInt();

                    if (!mapaSabores.containsKey(sabor)) {
                        mapaSabores.put(sabor, new float[12]);
                    }
                    mapaSabores.get(sabor)[index] += cantidad;
                }
            }

            respuestasRecibidas++;
            verificarFinalizacion();
        });
    }


    private void verificarFinalizacion() {
        if (respuestasRecibidas == 12) {
            // Ordenar reportes por mes (ya están ordenados por index)
            listaReportes.sort(Comparator.comparing(ReporteVenta::getTitulo));

            adapter = new ReporteAdapter(listaReportes);
            recyclerView.setAdapter(adapter);

            // Llenar gráfica de barras
            BarDataSet barDataSet = new BarDataSet(entradasBarras, "Pedidos por mes");
            barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
            ventasChart.setData(new BarData(barDataSet));
            ventasChart.animateY(1000);
            ventasChart.getDescription().setText("Pedidos mensuales");
            ventasChart.invalidate();

            // Llenar gráfica de líneas
            List<ILineDataSet> dataSets = new ArrayList<>();
//            int colorIndex = 0;
//            for (Map.Entry<String, float[]> entry : mapaSabores.entrySet()) {
//                ArrayList<Entry> entries = new ArrayList<>();
//                for (int i = 0; i < 12; i++) {
//                    entries.add(new Entry(i, entry.getValue()[i]));
//                }
//                LineDataSet set = new LineDataSet(entries, entry.getKey());
//                set.setColor(ColorTemplate.COLORFUL_COLORS[colorIndex % ColorTemplate.COLORFUL_COLORS.length]);
//                set.setCircleColor(set.getColor());
//                set.setDrawValues(true);
//                set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//                dataSets.add(set);
//                colorIndex++;
//            }

            for (Map.Entry<String, float[]> entry : mapaSabores.entrySet()) {
                ArrayList<Entry> entries = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    entries.add(new Entry(i, entry.getValue()[i]));
                }

                LineDataSet set = new LineDataSet(entries, entry.getKey());

                String sabor = entry.getKey().toLowerCase();
                switch (sabor) {
                    case "chocolate":
                        set.setColor(0xFF6F4E37); // color café
                        set.setCircleColor(0xFF6F4E37);
                        break;
                    case "fresa":
                        set.setColor(0xFFE53935); // rojo fuerte
                        set.setCircleColor(0xFFE53935);
                        break;
                    case "vainilla":
                        set.setColor(0xFFFFEB3B); // amarillo
                        set.setCircleColor(0xFFFFEB3B);
                        break;
                    default:
                        set.setColor(ColorTemplate.COLORFUL_COLORS[0]); // color por defecto
                        set.setCircleColor(ColorTemplate.COLORFUL_COLORS[0]);
                        break;
                }

                set.setDrawValues(true);
                set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                dataSets.add(set);
            }

            LineData lineData = new LineData(dataSets);
            saboresChart.setData(lineData);

            String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
            saboresChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    return (index >= 0 && index < meses.length) ? meses[index] : "";
                }
            });
            saboresChart.getXAxis().setGranularity(1f);
            saboresChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            saboresChart.getDescription().setText("Pedidos por sabor");
            saboresChart.animateX(2000);
            saboresChart.invalidate();
        }
    }

    private String obtenerNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }

}