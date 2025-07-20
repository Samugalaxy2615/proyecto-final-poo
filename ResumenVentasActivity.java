package com.example.mytiendita;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.*;

import java.util.*;

public class ResumenVentasActivity extends AppCompatActivity {

    private BarChart barChart;
    private PieChart pieChart;
    private TextView textSugerencias, textReabastecimiento;
    private LinearLayout layoutSugerencias, layoutReabastecimiento;
    private DatabaseReference ventasRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_ventas);

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        textSugerencias = findViewById(R.id.textSugerencias);
        layoutSugerencias = findViewById(R.id.layoutSugerencias);
        textReabastecimiento = findViewById(R.id.textReabastecimiento);
        layoutReabastecimiento = findViewById(R.id.layoutReabastecimiento);

        ventasRef = FirebaseDatabase.getInstance().getReference("ventas");

        cargarDatosDeVentas();
    }

    private void cargarDatosDeVentas() {
        ventasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> contadorVentas = new HashMap<>();

                for (DataSnapshot ventaSnap : snapshot.getChildren()) {
                    DataSnapshot productosSnap = ventaSnap.child("productos");
                    for (DataSnapshot prodSnap : productosSnap.getChildren()) {
                        String nombre = prodSnap.child("nombre").getValue(String.class);
                        Integer cantidad = prodSnap.child("cantidad").getValue(Integer.class);
                        if (nombre != null && cantidad != null) {
                            contadorVentas.put(nombre, contadorVentas.getOrDefault(nombre, 0) + cantidad);
                        }
                    }
                }

                if (contadorVentas.isEmpty()) {
                    Toast.makeText(ResumenVentasActivity.this, "No hay ventas registradas", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(contadorVentas.entrySet());
                listaOrdenada.sort((a, b) -> b.getValue() - a.getValue());

                StringBuilder sugerenciasTexto = new StringBuilder();
                sugerenciasTexto.append("📈 Más vendidos:\n");
                for (int i = 0; i < Math.min(3, listaOrdenada.size()); i++) {
                    String nombre = listaOrdenada.get(i).getKey();
                    int cantidad = listaOrdenada.get(i).getValue();
                    sugerenciasTexto.append("- ").append(nombre).append(" (").append(cantidad).append(" ventas)\n");
                }

                sugerenciasTexto.append("\n📉 Menos vendidos:\n");
                for (int i = listaOrdenada.size() - 1; i >= Math.max(listaOrdenada.size() - 3, 0); i--) {
                    String nombre = listaOrdenada.get(i).getKey();
                    int cantidad = listaOrdenada.get(i).getValue();
                    sugerenciasTexto.append("- ").append(nombre).append(" (solo ").append(cantidad).append(" ventas)\n");
                }

                new AlertDialog.Builder(ResumenVentasActivity.this)
                        .setTitle("Análisis de productos")
                        .setMessage(sugerenciasTexto.toString())
                        .setPositiveButton("OK", null)
                        .show();

                textSugerencias.setVisibility(View.VISIBLE);
                layoutSugerencias.setVisibility(View.VISIBLE);
                layoutSugerencias.removeAllViews();

                for (int i = listaOrdenada.size() - 1; i >= listaOrdenada.size() - 2 && i >= 0; i--) {
                    String nombre = listaOrdenada.get(i).getKey();
                    int cantidad = listaOrdenada.get(i).getValue();

                    TextView sugerencia = new TextView(ResumenVentasActivity.this);
                    sugerencia.setText("🔻 " + nombre + ": sugerir 10-20% de descuento (solo " + cantidad + " ventas)");
                    sugerencia.setTextSize(16f);
                    sugerencia.setTextColor(Color.BLACK);
                    sugerencia.setPadding(8, 8, 8, 8);
                    layoutSugerencias.addView(sugerencia);
                }

                textReabastecimiento.setVisibility(View.VISIBLE);
                layoutReabastecimiento.setVisibility(View.VISIBLE);
                layoutReabastecimiento.removeAllViews();

                for (int i = 0; i < Math.min(2, listaOrdenada.size()); i++) {
                    String nombre = listaOrdenada.get(i).getKey();
                    int cantidad = listaOrdenada.get(i).getValue();

                    TextView sugerencia = new TextView(ResumenVentasActivity.this);
                    sugerencia.setText("🛒 " + nombre + ": considera pedir más unidades (" + cantidad + " ventas)");
                    sugerencia.setTextSize(16f);
                    sugerencia.setTextColor(Color.BLACK);
                    sugerencia.setPadding(8, 8, 8, 8);
                    layoutReabastecimiento.addView(sugerencia);
                }

                mostrarGraficas(contadorVentas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ResumenVentasActivity.this, "Error al cargar ventas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarGraficas(Map<String, Integer> datos) {
        List<BarEntry> barEntries = new ArrayList<>();
        List<PieEntry> pieEntries = new ArrayList<>();
        List<String> nombresProductos = new ArrayList<>();

        List<Integer> colores = Arrays.asList(
                Color.rgb(128, 128, 128),
                Color.rgb(33, 150, 243),
                Color.rgb(255, 193, 7),
                Color.rgb(76, 175, 80),
                Color.rgb(156, 39, 176),
                Color.rgb(255, 87, 34),
                Color.rgb(63, 81, 181)
        );

        List<Integer> coloresUsados = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            String nombre = entry.getKey();
            int cantidad = entry.getValue();

            barEntries.add(new BarEntry(index, cantidad));
            pieEntries.add(new PieEntry(cantidad, nombre));
            nombresProductos.add(nombre);
            coloresUsados.add(colores.get(index % colores.size()));
            index++;
        }

        // BarChart
        BarDataSet barDataSet = new BarDataSet(barEntries, "PRODUCTOS VENDIDOS");
        barDataSet.setColors(coloresUsados);
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueTextColor(Color.WHITE);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setDrawLabels(true);
        barDataSet.setValueTextColor(Color.BLACK); //
        barChart.getXAxis().setTextColor(Color.BLACK);
        barChart.getAxisLeft().setTextColor(Color.BLACK);
        barChart.getAxisRight().setTextColor(Color.BLACK);
        barChart.getLegend().setTextColor(Color.BLACK);

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int) value;
                return (i >= 0 && i < nombresProductos.size()) ? nombresProductos.get(i) : "";
            }
        });
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

        // PieChart
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "<3");
        pieDataSet.setColors(coloresUsados);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.getLegend().setTextColor(Color.BLACK);
        pieChart.setDescription(new Description());
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }
}
