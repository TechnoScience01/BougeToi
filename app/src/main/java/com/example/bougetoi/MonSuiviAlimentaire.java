package com.example.bougetoi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AlertDialog;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

public class MonSuiviAlimentaire extends AppCompatActivity {

    private TableLayout tableLayout;
    private TextView actuellesCalories;
    private ProgressBar progressBar;
    private String[] aliments;
    private double[] caloriesParGramme;
    private double[] poidsParUnite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mon_suivi_alimentaire);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tableLayout = findViewById(R.id.tableLayout);
        Button addButton = findViewById(R.id.button);
        actuellesCalories = findViewById(R.id.actuellesCalories);
        progressBar = findViewById(R.id.progressBar);

        // Chargement des données depuis strings.xml
        aliments = getResources().getStringArray(R.array.aliments);
        String[] caloriesStr = getResources().getStringArray(R.array.calories_par_gramme);
        String[] poidsStr = getResources().getStringArray(R.array.poids_par_unite);

        // Conversion des chaînes en doubles
        caloriesParGramme = new double[caloriesStr.length];
        poidsParUnite = new double[poidsStr.length];
        for (int i = 0; i < caloriesStr.length; i++) {
            caloriesParGramme[i] = Double.parseDouble(caloriesStr[i]);
            poidsParUnite[i] = Double.parseDouble(poidsStr[i]);
        }

        // Initialise le texte des calories actuelles et la barre de progression
        if (actuellesCalories != null) {
            actuellesCalories.setText("0");
        }
        if (progressBar != null) {
            double caloriesMax = 2000; // Valeur maximale pour la ProgressBar
            progressBar.setMax((int) caloriesMax);
            progressBar.setProgress(0); // Initialisé à 0
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });

        // Ajout du gestionnaire pour le bouton Voir_macronutriments
        Button btnMacronutriments = findViewById(R.id.Voir_macronutriments);
        if (btnMacronutriments != null) {
            btnMacronutriments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMacronutrimentsDialog();
                }
            });
        }



    }

    private void showAddFoodDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_ajout_aliment, null);

        Spinner spinnerAliment = dialogView.findViewById(R.id.spinnerAliment);
        EditText editTextQuantite = dialogView.findViewById(R.id.editTextQuantite);
        Spinner spinnerUnite = dialogView.findViewById(R.id.spinnerUnite);
        TextView textViewCalories = dialogView.findViewById(R.id.textViewCalories);

        // Liste des aliments depuis strings.xml
        ArrayAdapter<String> alimentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, aliments);
        alimentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAliment.setAdapter(alimentAdapter);

        // Liste des unités
        String[] unites = {"g", "kg", "mL", "L", "Unité"};
        ArrayAdapter<String> uniteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unites);
        uniteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnite.setAdapter(uniteAdapter);

        // Calcule les calories en temps réel
        AdapterView.OnItemSelectedListener calorieUpdater = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCalories(spinnerAliment, editTextQuantite, spinnerUnite, textViewCalories);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerAliment.setOnItemSelectedListener(calorieUpdater);
        spinnerUnite.setOnItemSelectedListener(calorieUpdater);
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

        // Crée la boîte de dialogue

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            int alimentIndex = spinnerAliment.getSelectedItemPosition();
            String aliment = aliments[alimentIndex];
            String quantite = editTextQuantite.getText().toString().trim();
            String unite = spinnerUnite.getSelectedItem().toString();
            if (!quantite.isEmpty()) {
                String calories = calculateCalories(alimentIndex, quantite, unite);
                addTableRow(aliment, quantite + " " + unite, calories);
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
            String calories = calculateCalories(alimentIndex, quantiteStr, unite);
            textViewCalories.setText("Calories : " + calories);
        } else {
            textViewCalories.setText("Calories : 0");
        }
    }

    private String calculateCalories(int alimentIndex, String quantiteStr, String unite) {
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

    private void addTableRow(String aliment, String quantite, String calories) {
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
        deleteButton.setPadding(8, 0, 8, 0); // Réduit la taille du bouton
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MonSuiviAlimentaire.this)
                        .setTitle("Supprimer")
                        .setMessage("Voulez-vous supprimer cet aliment ?")
                        .setPositiveButton("Oui", (dialog, which) -> {
                            tableLayout.removeView(tableRow);
                            updateTotalAndProgress();
                        })
                        .setNegativeButton("Non", null)
                        .show();
            }
        });
        tableRow.addView(deleteButton);

        tableLayout.addView(tableRow);
        tableLayout.requestLayout();

        // Met à jour le total et la ProgressBar
        updateTotalAndProgress();
    }

    private void updateTotalAndProgress() {
        if (actuellesCalories != null) {
            Double totalCalories = calculNbCaloriesTotales();
            actuellesCalories.setText(totalCalories.toString());
        }
        if (progressBar != null) {
            double totalCalories = calculNbCaloriesTotales();
            double caloriesMax = 2000; // Valeur maximale pour la ProgressBar
            progressBar.setMax((int) caloriesMax);
            progressBar.setProgress((int) totalCalories);
        }
    }

    private Double calculNbCaloriesTotales() {
        double totalCalories = 0.0;
        // Commence à 1 pour ignorer la ligne d'en-tête
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            TextView caloriesTextView = (TextView) row.getChildAt(2); // Colonne des calories
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

    private void showMacronutrimentsDialog() {
        // Calculer les macronutriments en fonction des aliments ajoutés
        double totalCalories = calculNbCaloriesTotales();
        // Ces valeurs sont approximatives, vous devriez ajuster selon votre application
        double totalProtein = totalCalories * 0.25 / 4; // 25% des calories, 4 calories par gramme
        double totalCarbs = totalCalories * 0.50 / 4;   // 50% des calories, 4 calories par gramme
        double totalFat = totalCalories * 0.25 / 9;     // 25% des calories, 9 calories par gramme

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Macronutriments");

        // Créer une vue pour le dialogue
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        TextView textView = new TextView(this);
        textView.setPadding(20, 20, 20, 20);
        textView.setText(
                "Calories totales: " + String.format("%.0f", totalCalories) + " kcal\n" +
                        "Protéines: " + String.format("%.1f", totalProtein) + " g\n" +
                        "Glucides: " + String.format("%.1f", totalCarbs) + " g\n" +
                        "Lipides: " + String.format("%.1f", totalFat) + " g"
        );

        builder.setView(textView);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}