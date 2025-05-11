package com.example.bougetoi;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.bougetoi.databinding.ActivitySuiviMorphoBinding;

public class SuiviMorpho extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText inputPoids;

    private ActivitySuiviMorphoBinding binding;
    private LineChart lineChart;

    private float[] poidsMesuresJson = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySuiviMorphoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_suivi_morpho);
        binding.flecheRetour.setOnClickListener(this);

        ExecutorService executer = Executors.newSingleThreadExecutor();
        executer.execute(new Runnable() {
            public void run() {
                morphodata Morphodata = JsonReader.convertJsonToObject(SuiviMorpho.this);

                runOnUiThread(new Runnable() {
                    public void run() {

                                List<String> poids = Morphodata.getPoids();
                                Log.d("DEBUG_MORPHO", "Poids JSON : " + poids);
                                // ou stocker dans une variable membre pour l'utiliser plus tard

                    }
                });
            }
        });


        // Saisie du poids
        inputPoids = findViewById(R.id.TI_poids);
        lineChart = findViewById(R.id.lineChart);

        findViewById(R.id.btn_valider_poids).setOnClickListener(v -> updateGraph());
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

                    // Ajout si le poids n'est pas déjà présent
                    List<Float> poidsExistants = JsonReader.getPoidsFromJson(this);
                    if (poidsExistants.isEmpty() || poidsDuJour != poidsExistants.get(0)) {
                        JsonReader.pushPoids(this, poidsDuJour);
                        Log.d("SuiviMorpho", "Poids ajouté : " + poidsDuJour);
                    } else {
                        Log.d("SuiviMorpho", "Poids déjà présent, pas ajouté.");
                    }
                }
            }
        } catch (NumberFormatException e) {
            poidsDispo = false;
        }

        List<Float> poidsList = JsonReader.getPoidsFromJson(this);

        float[] poidsMesures;
        if (!poidsList.isEmpty()) {
            poidsMesures = new float[poidsList.size()];
            for (int i = 0; i < poidsList.size(); i++) {
                poidsMesures[i] = poidsList.get(i);
            }
        } else {
            poidsMesures = new float[30];
            for (int i = 0; i < 30; i++) {
                poidsMesures[i] = 0f;
            }
        }

        float[] poidsAffiches = poidsMesures;

        // Inversion de l’ordre pour affichage du plus ancien au plus récent
        float[] poidsInverses = new float[poidsAffiches.length];
        for (int i = 0; i < poidsAffiches.length; i++) {
            poidsInverses[i] = poidsAffiches[poidsAffiches.length - 1 - i];
        }

        // Générer les dates correspondantes dans le même ordre
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        String[] dates = new String[poidsInverses.length];

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -(poidsInverses.length - 1));

        for (int i = 0; i < poidsInverses.length; i++) {
            dates[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < poidsInverses.length; i++) {
            entries.add(new Entry(i, poidsInverses[i]));
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
        for (float poids : poidsInverses) {
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

        lineChart.invalidate(); // Redessine le graphe
    }

    public void onClick(View view) {
        int id = view.getId();
       if (id == R.id.flecheRetour) {
            // Terminer cette activité pour revenir en arrière
            finish();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
