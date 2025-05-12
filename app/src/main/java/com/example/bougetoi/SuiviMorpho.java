package com.example.bougetoi;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;


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

    private CheckBox checkBoxNotifications;
    private Spinner spinnerHumeur;
    private float[] poidsMesuresJson = null;

    private TextView tvImc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuiviMorphoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkBoxNotifications = findViewById(R.id.CB_Notifications);
        checkBoxNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                NotificationUtils.sendNotification(this, "N'oubliez pas de rentrer votre poids");

                NotificationUtils.scheduleDailyNotification(this);
            } else {
                NotificationUtils.cancelDailyNotification(this);
            }
        });

        spinnerHumeur = findViewById(R.id.spinnerHumeur);
        tvImc = findViewById(R.id.TV_imc);
        binding.flecheRetour.setOnClickListener(this);

        ExecutorService executer = Executors.newSingleThreadExecutor();
        executer.execute(() -> {
            MorphoData morphodata = JsonReader.convertJsonToObject(SuiviMorpho.this);

            runOnUiThread(() -> {
                if (morphodata != null && morphodata.getPoids() != null) {
                    List<String> poids = morphodata.getPoids();
                    Log.d("DEBUG_MORPHO", "Poids JSON : " + poids);
                } else {
                    Log.w("DEBUG_MORPHO", "Erreur : morphodata ou getPoids() est null");
                }
            });
        });



        inputPoids = findViewById(R.id.TI_poids);
        Float dernierPoids = JsonReader.getDernierPoids(this);
        if (dernierPoids != null && dernierPoids > 0) {
            inputPoids.setText(String.format(Locale.getDefault(), "%.1f", dernierPoids));

            float taille = JsonReader.getTaille(this);
            if (taille > 10f) taille = taille / 100f;  // conversion cm → m
            if (taille <= 0f) taille = 1.60f;

            float imc = dernierPoids / (taille * taille);
            tvImc.setText(String.format(Locale.getDefault(), "%.2f", imc));

        } else {
            tvImc.setText("0.0");
        }

        lineChart = findViewById(R.id.lineChart);

        findViewById(R.id.btn_valider_poids).setOnClickListener(v -> updateGraph());
        inputPoids.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) updateGraph();
        });

        inputPoids.setOnEditorActionListener((v, actionId, event) -> {
            updateGraph();
            return false;
        });

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
        String humeurSauvegardee = JsonReader.getDerniereHumeur(this);
        if (humeurSauvegardee != null) {
            int position = adapter.getPosition(humeurSauvegardee);
            if (position >= 0) {
                spinnerHumeur.setSelection(position);
            }
        }


        updateGraph();
        TextView objectifPoidsTextView = findViewById(R.id.TV_objectif_Poids);
        float objectifPoids = JsonReader.getObjectifPoids(this);
        objectifPoidsTextView.setText(String.format(Locale.getDefault(), "Votre objectif de poids est de : %.1f kg", objectifPoids));

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

                    List<Float> poidsExistants = JsonReader.getPoidsFromJson(this);
                    if (poidsExistants.isEmpty() || poidsDuJour != poidsExistants.get(0)) {
                        JsonReader.pushPoids(this, poidsDuJour);
                        String humeurDuJour = (String) spinnerHumeur.getSelectedItem();
                        if (humeurDuJour != null && !humeurDuJour.isEmpty()) {
                            JsonReader.pushHumeur(this, humeurDuJour);
                            Log.d("SuiviMorpho", "Humeur ajoutée : " + humeurDuJour);
                        }
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

        float[] poidsInverses = new float[poidsAffiches.length];
        for (int i = 0; i < poidsAffiches.length; i++) {
            poidsInverses[i] = poidsAffiches[poidsAffiches.length - 1 - i];
        }

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
        float objectifPoids = JsonReader.getObjectifPoids(this);
        if (objectifPoids <= 0f) {
            objectifPoids = 70f; // valeur par défaut si absente
            Log.w("SuiviMorpho", "Objectif poids manquant, 70 kg utilisé par défaut");
        }

        LimitLine objectifLine = new LimitLine(objectifPoids, "Objectif " + objectifPoids + " kg");
        objectifLine.setLineColor(Color.RED);
        objectifLine.setLineWidth(2);
        objectifLine.setTextColor(Color.RED);
        objectifLine.setTextSize(12);
        objectifLine.enableDashedLine(10, 10, 0);
        yAxis.addLimitLine(objectifLine);


        lineChart.invalidate();
        try {
            float poids = Float.parseFloat(saisie);
            float taille = JsonReader.getTaille(this);
            if (taille > 10f) taille = taille / 100f;
            if (taille <= 0f) taille = 1.60f;

            float imc = poids / (taille * taille);
            tvImc.setText(String.format(Locale.getDefault(), "%.2f", imc));

        } catch (Exception e) {
            tvImc.setText("0.0");
            Log.w("SuiviMorpho", "Impossible de calculer l'IMC : " + e.getMessage());
        }
    }

    public void onClick(View view) {
        int id = view.getId();
       if (id == R.id.flecheRetour) {
            finish();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
