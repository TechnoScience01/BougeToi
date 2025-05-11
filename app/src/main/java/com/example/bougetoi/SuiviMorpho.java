package com.example.bougetoi;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuiviMorpho extends AppCompatActivity {

    private TextInputEditText inputPoids;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suivi_morpho);

        ExecutorService executer = Executors.newSingleThreadExecutor();
        executer.execute(new Runnable() {
            public void run() {
                morphodata morphodata = JsonReader.convertJsonToObject(SuiviMorpho.this);

                runOnUiThread(new Runnable() {
                    public void run() {
                        // écrire ici pour récupérer la data json getter et setter morphodata
                    }
                });
            }
        });


        // Saisie du poids
        inputPoids = findViewById(R.id.TI_poids);
        lineChart = findViewById(R.id.lineChart);

        // Rafraîchit le graphe quand on sort du champ ou qu'on appuie sur "Entrée"
        inputPoids.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) updateGraph();
        });

        inputPoids.setOnEditorActionListener((v, actionId, event) -> {
            updateGraph();
            return false;
        });

        // Spinner humeur
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

        // Affichage initial du graphe
        updateGraph();
    }

    private void updateGraph() {
        String saisie = inputPoids.getText() != null ? inputPoids.getText().toString() : "";
        float poidsDuJour = -1;
        boolean poidsDispo = false;

        try {
            if (!saisie.isEmpty()) {
                poidsDuJour = Float.parseFloat(saisie);
                if (poidsDuJour > 0) {
                    poidsDispo = true;
                }
            }
        } catch (NumberFormatException e) {
            poidsDispo = false;
        }

        float[] poidsMesures = {
                72.5f, 72.3f, 72.1f, 72.0f, 71.9f, 71.7f, 71.6f,
                71.5f, 71.3f, 71.2f, 71.1f, 71.0f, 70.9f, 70.8f,
                70.8f, 70.7f, 70.6f, 70.6f, 70.5f, 70.4f, 70.4f,
                70.3f, 70.2f, 70.1f, 70.0f, 69.9f, 69.8f, 69.7f,
                69.6f, 69.5f
        };

        float[] poidsAffiches;
        if (poidsDispo) {
            poidsAffiches = new float[poidsMesures.length];
            System.arraycopy(poidsMesures, 1, poidsAffiches, 0, poidsMesures.length - 1);
            poidsAffiches[poidsAffiches.length - 1] = poidsDuJour;
        } else {
            poidsAffiches = new float[poidsMesures.length - 1];
            System.arraycopy(poidsMesures, 1, poidsAffiches, 0, poidsAffiches.length);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        String[] dates = new String[poidsAffiches.length];

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -(poidsAffiches.length - 1));

        for (int i = 0; i < poidsAffiches.length; i++) {
            dates[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < poidsAffiches.length; i++) {
            entries.add(new Entry(i, poidsAffiches[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Poids");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(80);
        dataSet.setFillColor(Color.CYAN);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2);
        dataSet.setDrawCircles(false);

        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = lineChart.getAxisLeft();
        float minPoids = Float.MAX_VALUE;
        float maxPoids = Float.MIN_VALUE;
        for (float poids : poidsAffiches) {
            if (poids < minPoids) minPoids = poids;
            if (poids > maxPoids) maxPoids = poids;
        }

        float marge = 1.0f;
        yAxis.setAxisMinimum(minPoids - marge);
        yAxis.setAxisMaximum(maxPoids + marge);

        yAxis.removeAllLimitLines();
        LimitLine objectifLine = new LimitLine(70, "Objectif 70 kg");
        objectifLine.setLineColor(Color.RED);
        objectifLine.setLineWidth(2);
        objectifLine.setTextColor(Color.RED);
        objectifLine.setTextSize(12);
        objectifLine.enableDashedLine(10, 10, 0);
        yAxis.addLimitLine(objectifLine);

        lineChart.invalidate(); // refresh visuel
    }
}
