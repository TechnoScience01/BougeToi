package com.example.bougetoi;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class SuiviMorpho extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suivi_morpho);
        // affichage humeur
        Spinner spinnerHumeur = findViewById(R.id.spinnerHumeur);

        List<String> humeurs = new ArrayList<>();
        humeurs.add("😊 Bonne humeur");
        humeurs.add("😐 Moyenne");
        humeurs.add("😞 Fatigué");
        humeurs.add("😠 Stressé");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, humeurs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerHumeur.setAdapter(adapter);

        // graphique de suivi de poids

        LineChart lineChart = findViewById(R.id.lineChart);
        if (lineChart == null) {
            Log.e("ChartDebug", "lineChart is NULL !");
            return;
        }
        /*
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entries.add(new Entry(i, i * 2));
        }
        */
        List<Entry> entries = new ArrayList<>();
        float[] poidsMesures = {
                72.5f, 72.3f, 72.1f, 72.0f, 71.9f, 71.7f, 71.6f,
                71.5f, 71.3f, 71.2f, 71.1f, 71.0f, 70.9f, 70.8f,
                70.8f, 70.7f, 70.6f, 70.6f, 70.5f, 70.4f, 70.4f,
                70.3f, 70.2f, 70.1f, 70.0f, 69.9f, 69.8f, 69.7f,
                69.6f, 69.5f
        };

        for (int i = 0; i < poidsMesures.length; i++) {
            entries.add(new Entry(i, poidsMesures[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Poids");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(80);
        dataSet.setFillColor(Color.CYAN); // ou un gradient si tu veux aller plus loin
        dataSet.setDrawValues(true);
        dataSet.setLineWidth(2);


        LineData lineData = new LineData(dataSet);


        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(true);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(65); // ajuster selon la fourchette de poids
        yAxis.setAxisMaximum(75);

        LimitLine objectifLine = new LimitLine(70, "Objectif 70 kg");
        objectifLine.setLineColor(Color.RED);
        objectifLine.setLineWidth(2);
        objectifLine.setTextColor(Color.RED);
        objectifLine.setTextSize(12);
        objectifLine.enableDashedLine(10, 10, 0);
        yAxis.addLimitLine(objectifLine);

        lineChart.invalidate(); // rafraîchit le graphe
    }
}
