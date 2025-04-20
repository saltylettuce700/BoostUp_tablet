package com.example.boostup_tablet.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boostup_tablet.Adapter.ReporteAdapter;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import POJO.ReporteVenta;

public class reporte_ventas_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReporteAdapter adapter;
    private List<ReporteVenta> listaReportes;

    BarChart ventasChart;
    LineChart saboresChart;


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

        ventasChart = findViewById(R.id.ventas_chart);
        saboresChart = findViewById(R.id.sabores_chart);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crear datos de ejemplo
        listaReportes = new ArrayList<>();
        listaReportes.add(new ReporteVenta("Reporte de venta Mensual - Enero 2024", "$10,000", "150 pedidos"));
        listaReportes.add(new ReporteVenta("Reporte de venta Mensual - Febrero 2024", "$8,500", "120 pedidos"));
        listaReportes.add(new ReporteVenta("Reporte de venta Mensual - Marzo 2024", "$9,200", "130 pedidos"));

        adapter = new ReporteAdapter(listaReportes);
        recyclerView.setAdapter(adapter);

        //Datos de la grafica

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int i = 1; i <13; i++){
            //convert to float

            float value = (float) (i*13.0);
            //initialize bar chart entry

            BarEntry barEntry = new BarEntry(i,value);
            barEntries.add(barEntry);
        }

        //initialize bar data set

        BarDataSet barDataSet = new BarDataSet(barEntries, "Meses del año");
        //set color

        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        //set bar data
        ventasChart.setData(new BarData(barDataSet));

        //Set animation

        ventasChart.animateY(5000);

        ventasChart.getDescription().setText("Ventas anuales");

        //grafica de lineas

        // Datos simulados por sabor para cada mes (enero = 0, diciembre = 11)
        float[] pedidosFresa = {12, 18, 15, 20, 25, 30, 28, 27, 24, 22, 19, 21};
        float[] pedidosChocolate = {10, 14, 13, 17, 23, 25, 29, 26, 20, 18, 16, 19};
        float[] pedidosVainilla = {8, 12, 11, 14, 18, 21, 20, 22, 19, 15, 13, 17};

        ArrayList<Entry> entriesFresa = new ArrayList<>();
        ArrayList<Entry> entriesChocolate = new ArrayList<>();
        ArrayList<Entry> entriesVainilla = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            entriesFresa.add(new Entry(i, pedidosFresa[i]));
            entriesChocolate.add(new Entry(i, pedidosChocolate[i]));
            entriesVainilla.add(new Entry(i, pedidosVainilla[i]));
        }

        LineDataSet dataSetFresa = new LineDataSet(entriesFresa, "Fresa");
        dataSetFresa.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        dataSetFresa.setCircleColor(ColorTemplate.COLORFUL_COLORS[0]);

        LineDataSet dataSetChocolate = new LineDataSet(entriesChocolate, "Chocolate");
        dataSetChocolate.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        dataSetChocolate.setCircleColor(ColorTemplate.COLORFUL_COLORS[1]);

        LineDataSet dataSetVainilla = new LineDataSet(entriesVainilla, "Vainilla");
        dataSetVainilla.setColor(ColorTemplate.COLORFUL_COLORS[2]);
        dataSetVainilla.setCircleColor(ColorTemplate.COLORFUL_COLORS[2]);

        LineData lineData = new LineData(dataSetFresa, dataSetChocolate, dataSetVainilla);

        saboresChart.setData(lineData);

        // Configurar ejes
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
        saboresChart.invalidate(); // refrescar

        dataSetFresa.setDrawValues(true);
        dataSetChocolate.setDrawValues(true);
        dataSetVainilla.setDrawValues(true);

        dataSetFresa.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetChocolate.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetVainilla.setMode(LineDataSet.Mode.CUBIC_BEZIER);


    }
}