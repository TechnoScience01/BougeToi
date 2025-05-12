package com.example.bougetoi;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.bougetoi.databinding.ActivityMonSuiviAlimentaireBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class MonSuiviAlimentaire extends AppCompatActivity {

    private TableLayout tableLayout;
    private String[] aliments;
    private double[] caloriesParGramme;
    private double[] poidsParUnite;
    private ActivityMonSuiviAlimentaireBinding binding;
    private String selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMonSuiviAlimentaireBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Set click listeners
        binding.backArrow.setOnClickListener(v -> finish());
        binding.AjoutAliment.setOnClickListener(v -> popupAjoutAliments());
        binding.btnVoirMacronutrients.setOnClickListener(v -> popupMacronutriments());

        DatePicker datePicker = findViewById(R.id.datePicker);
        LocalDate today = LocalDate.now();
        datePicker.updateDate(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth());
        selectedDate = today.toString();

        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth); // ✅ ICI
                    afficherSuiviPourDate(selectedDate);
                }
        );


        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tableLayout = binding.tableLayout;

        List<SuiviJournalier> suivis = JsonReader.getSuivisFromJson(this);
        String dateDuJour = java.time.LocalDate.now().toString(); // API >= 26

        for (SuiviJournalier suivi : suivis) {
            if (suivi.date.equals(dateDuJour)) {
                restaurerSuivi(suivi);
                break;
            }
        }

        aliments = getResources().getStringArray(R.array.aliments);
        String[] caloriesStr = getResources().getStringArray(R.array.calories_par_gramme);
        String[] poidsStr = getResources().getStringArray(R.array.poids_par_unite);

        if (aliments.length != caloriesStr.length || aliments.length != poidsStr.length) {
            throw new IllegalStateException("nombre d'item dans les tableaux différents");
        }

        caloriesParGramme = new double[caloriesStr.length];
        poidsParUnite = new double[poidsStr.length];
        for (int i = 0; i < caloriesStr.length; i++) {
            caloriesParGramme[i] = Double.parseDouble(caloriesStr[i]);
            poidsParUnite[i] = Double.parseDouble(poidsStr[i]);
        }

        float objectifCalorique = JsonReader.getObjectifCalorique(this);
        binding.objectifCalories.setText(String.format("%.0f", objectifCalorique));
        String texteObjectif = " / "+String.format("%.0f", objectifCalorique);
        binding.nbObjectifCalories.setText(texteObjectif);

        binding.actuellesCalories.setText("0");

        binding.progressBar.setMax((int) objectifCalorique);

        binding.progressBar.setProgress(0);


    }

    private void popupAjoutAliments() {
        LayoutInflater inflater = this.getLayoutInflater();
        View popupAjout = inflater.inflate(R.layout.popup_ajout_aliment, null);

        Spinner spinnerAliment = popupAjout.findViewById(R.id.spinnerAliment);
        EditText editTextQuantite = popupAjout.findViewById(R.id.editTextQuantite);
        Spinner spinnerUnite = popupAjout.findViewById(R.id.spinnerUnite);
        TextView textViewCalories = popupAjout.findViewById(R.id.textViewCalories);

        ArrayAdapter<String> alimentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, aliments);
        alimentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAliment.setAdapter(alimentAdapter);

        String[] unites = {"g", "kg", "mL", "L", "Unité"};
        ArrayAdapter<String> uniteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unites);
        uniteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnite.setAdapter(uniteAdapter);

        AdapterView.OnItemSelectedListener updateCalories = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCalories(spinnerAliment, editTextQuantite, spinnerUnite, textViewCalories);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerAliment.setOnItemSelectedListener(updateCalories);
        spinnerUnite.setOnItemSelectedListener(updateCalories);
        editTextQuantite.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateCalories(spinnerAliment, editTextQuantite, spinnerUnite, textViewCalories);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupAjout);
        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            int alimentIndex = spinnerAliment.getSelectedItemPosition();
            String aliment = aliments[alimentIndex];
            String quantite = editTextQuantite.getText().toString().trim();
            String unite = spinnerUnite.getSelectedItem().toString();
            if (!quantite.isEmpty()) {
                String calories = calculerCalories(alimentIndex, quantite, unite);
                ajoutLigneAliments(aliment, quantite + " " + unite, calories);
            }
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void updateCalories(Spinner spinnerAliment, EditText editTextQuantite, Spinner spinnerUnite, TextView textViewCalories) {
        int alimentIndex = spinnerAliment.getSelectedItemPosition();
        String quantiteStr = editTextQuantite.getText().toString().trim();
        String unite = spinnerUnite.getSelectedItem().toString();

        if (!quantiteStr.isEmpty()) {
            String calories = calculerCalories(alimentIndex, quantiteStr, unite);
            textViewCalories.setText("Calories : " + calories);
        } else {
            textViewCalories.setText("Calories : 0");
        }
    }

    @SuppressLint("DefaultLocale")
    private String calculerCalories(int alimentIndex, String quantiteStr, String unite) {
        double quantite;
        try {
            quantite = Double.parseDouble(quantiteStr);
        } catch (NumberFormatException e) {
            return "0";
        }

        double caloriesBase = caloriesParGramme[alimentIndex];
        double facteurUnite;

        switch (unite) {
            case "kg":
                facteurUnite = 1000; // 1 kg = 1000 g
                break;
            case "L":
                facteurUnite = 1000; // 1 L = 1000 mL
                break;
            case "mL":
                facteurUnite = 1; // mL traité comme g pour simplifier
                break;
            case "Unité":
                facteurUnite = poidsParUnite[alimentIndex]; // Poids moyen par unité
                break;
            case "g":
            default:
                facteurUnite = 1;
                break;
        }

        double calories = caloriesBase * quantite * facteurUnite;
        return String.format("%.0f", calories); // Arrondi à l'entier
    }

    private void ajoutLigneAliments(String aliment, String quantite, String calories) {
        TableRow tableRow = new TableRow(this);

        TextView alimentTextView = new TextView(this);
        alimentTextView.setText(aliment);
        alimentTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tableRow.addView(alimentTextView);

        TextView quantiteTextView = new TextView(this);
        quantiteTextView.setText(quantite);
        quantiteTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tableRow.addView(quantiteTextView);

        TextView caloriesTextView = new TextView(this);
        caloriesTextView.setText(calories);
        caloriesTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tableRow.addView(caloriesTextView);

        // Ajout du bouton "Supprimer"
        Button deleteButton = new Button(this);
        deleteButton.setText("X");
        deleteButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        deleteButton.setPadding(0, 0, 4, 0);


        deleteButton.setOnClickListener(v -> new AlertDialog.Builder(MonSuiviAlimentaire.this)
                .setTitle("Supprimer")
                .setMessage("Voulez-vous supprimer cet aliment ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    tableLayout.removeView(tableRow);
                    updateTotalCalories();
                    // Recalculer et sauvegarder
                    SuiviJournalier suivi = construireSuiviJournalier();
                    JsonReader.saveSuiviToJson(this, suivi);


                })
                .setNegativeButton("Non", null)
                .show());
        tableRow.addView(deleteButton);

        tableLayout.addView(tableRow);
        tableLayout.requestLayout();

        // Met à jour le total et la ProgressBar
        updateTotalCalories();

        // Sauvegarde des données
        SuiviJournalier suivi = construireSuiviJournalier();
        JsonReader.saveSuiviToJson(this, suivi);


    }

    @SuppressLint("DefaultLocale")
    private void updateTotalCalories() {
        double totalCalories = calculNbCaloriesTotales();
        binding.actuellesCalories.setText(String.format("%.0f", totalCalories));
        double caloriesMax = 2000; // TODO: Make configurable
        binding.progressBar.setMax((int) caloriesMax);
        binding.progressBar.setProgress((int) totalCalories);
    }

    private double calculNbCaloriesTotales() {
        double totalCalories = 0.0;
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            TextView caloriesTextView = (TextView) row.getChildAt(2);
            String caloriesStr = caloriesTextView.getText().toString();
            if (!caloriesStr.isEmpty()) {
                try {
                    totalCalories += Double.parseDouble(caloriesStr);
                } catch (NumberFormatException e) {
                    // Ignore les valeurs invalides
                }
            }
        }
        return totalCalories;
    }


    private void popupMacronutriments() {
        double totalCalories = calculNbCaloriesTotales();
        double totalProteines = totalCalories * 0.25 / 4; // 25% des calories, 4 calories par gramme
        double totalGlucides = totalCalories * 0.50 / 4;   // 50% des calories, 4 calories par gramme
        double totalLipides = totalCalories * 0.25 / 9;     // 25% des calories, 9 calories par gramme

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Macronutriments");

        TextView textView = new TextView(this);
        textView.setPadding(20, 20, 20, 20);
        textView.setText(
                "Calories totales: " + String.format("%.0f", totalCalories) + " kcal\n" +
                        "Protéines: " + String.format("%.1f", totalProteines) + " g\n" +
                        "Glucides: " + String.format("%.1f", totalGlucides) + " g\n" +
                        "Lipides: " + String.format("%.1f", totalLipides) + " g"
        );

        builder.setView(textView);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //PArtie sauvegarde

    public static class SuiviJournalier {
        String date; // format AAAA-MM-JJ
        List<AlimentConsomme> aliments = new ArrayList<>();

        public static class AlimentConsomme {
            String nom;
            String quantite;
            String calories;

            public AlimentConsomme(String nom, String quantite, String calories) {
                this.nom = nom;
                this.quantite = quantite;
                this.calories = calories;
            }
        }
    }

    private SuiviJournalier construireSuiviJournalier() {
        SuiviJournalier suivi = new SuiviJournalier();
        suivi.date = selectedDate;

        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            if (row.getChildCount() < 3) continue;

            TextView tvAliment = (TextView) row.getChildAt(0);
            TextView tvQuantite = (TextView) row.getChildAt(1);
            TextView tvCalories = (TextView) row.getChildAt(2);

            SuiviJournalier.AlimentConsomme aliment = new SuiviJournalier.AlimentConsomme(
                    tvAliment.getText().toString(),
                    tvQuantite.getText().toString(),
                    tvCalories.getText().toString()
            );
            suivi.aliments.add(aliment);
        }

        return suivi;
    }


    private void restaurerSuivi(SuiviJournalier suivi) {
        // Supprime toutes les lignes sauf l'en-tête
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);

        for (SuiviJournalier.AlimentConsomme aliment : suivi.aliments) {
            ajoutLigneAliments(aliment.nom, aliment.quantite, aliment.calories);
        }
    }

    private void afficherSuiviPourDate(String date) {
        List<SuiviJournalier> suivis = JsonReader.getSuivisFromJson(this);

        for (SuiviJournalier suivi : suivis) {
            if (suivi.date != null && suivi.date.equals(date)) {
                // Nettoie la table actuelle (garde l'en-tête)
                tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
                // Affiche les aliments du jour sélectionné
                for (SuiviJournalier.AlimentConsomme aliment : suivi.aliments) {
                    ajoutLigneAliments(aliment.nom, aliment.quantite, aliment.calories);
                }
                updateTotalCalories();
                return;
            }
        }

        // Si aucun suivi trouvé
        Toast.makeText(this, "Aucun suivi trouvé pour cette date", Toast.LENGTH_SHORT).show();
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
        updateTotalCalories();
    }




}
