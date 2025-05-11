package com.example.bougetoi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Popup_ajout_seance extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout exercisesContainer;
    private ScrollView exercisesScrollView;
    private TextView exercisesHeader;
    private LinearLayout exerciseButtonsLayout;
    private Button cancelExercisesButton;
    private Button confirmExercisesButton;
    private TextView selectedExercisesText1;
    private TextView selectedExercisesText2;
    private List<String> selectedExercisesList = new ArrayList<>();

    // Variables pour la date
    private TextView selectedDate;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private Date chosenDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_ajout_seance);

        // Initialiser le calendrier et le format de date
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        chosenDate = calendar.getTime();

        // Initialiser tous les éléments du layout
        initializeViews();
        setupSpinner();
        setupButtons();
        setupDatePicker();
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.seanceTypeSpinner);

        // Créer l'adapter pour le spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.seance_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();

                // Réinitialiser les exercices
                exercisesContainer.removeAllViews();
                selectedExercisesList.clear();
                updateSelectedExercisesText();

                if (selectedType.equals("Musculation")) {
                    showExercisesViews(true);
                    String[] exercises = getResources().getStringArray(R.array.seance_musculation);
                    addExercises(exercisesContainer, exercises);
                } else if (selectedType.equals("Running")) {
                    showExercisesViews(true);
                    String[] exercises = getResources().getStringArray(R.array.seance_running);
                    addExercises(exercisesContainer, exercises);
                } else {
                    showExercisesViews(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                showExercisesViews(false);
            }
        });
    }
    private void initializeViews() {
        // Récupérer les références aux vues
        exercisesContainer = findViewById(R.id.exercisesContainer);
        exercisesScrollView = findViewById(R.id.exercisesScrollView);
        exercisesHeader = findViewById(R.id.exercisesHeader);
        exerciseButtonsLayout = findViewById(R.id.exerciseButtonsLayout);
        cancelExercisesButton = findViewById(R.id.cancelExercisesButton);
        confirmExercisesButton = findViewById(R.id.confirmExercisesButton);
        selectedExercisesText1 = findViewById(R.id.selectedExercisesText1);
        selectedExercisesText2 = findViewById(R.id.selectedExercisesText2);
        selectedDate = findViewById(R.id.selectedDate);
    }

    private void setupDatePicker() {
        // Afficher la date actuelle
        selectedDate.setText(dateFormat.format(calendar.getTime()));

        // Rendre le TextView cliquable
        selectedDate.setOnClickListener(v -> {
            // Créer un DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        // Mettre à jour le calendrier
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        chosenDate = calendar.getTime();

                        // Mettre à jour l'affichage
                        selectedDate.setText(dateFormat.format(chosenDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupButtons() {
        Button confirmerButton = findViewById(R.id.confirmerButton);
        Button annulerButton = findViewById(R.id.annulerButton);

        confirmerButton.setOnClickListener(v -> {
            // Récupérer les données de la séance
            String nomSeance = ((EditText) findViewById(R.id.nomSeance)).getText().toString();
            String dureeStr = ((EditText) findViewById(R.id.dureeSeance)).getText().toString();
            String description = ((EditText) findViewById(R.id.descriptionSeance)).getText().toString();
            String typeSeance = ((Spinner) findViewById(R.id.seanceTypeSpinner)).getSelectedItem().toString();

            // Validation des champs obligatoires
            if (nomSeance.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir un nom pour la séance", Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer l'intent de retour avec les données
            Intent resultIntent = new Intent();
            resultIntent.putExtra("NOM_SEANCE", nomSeance);
            resultIntent.putExtra("DATE_SEANCE", dateFormat.format(chosenDate));
            resultIntent.putExtra("DATE_TIMESTAMP", chosenDate.getTime());
            if (!dureeStr.isEmpty()) {
                resultIntent.putExtra("DUREE_SEANCE", Integer.parseInt(dureeStr));
            }
            resultIntent.putExtra("DESCRIPTION_SEANCE", description);
            resultIntent.putExtra("TYPE_SEANCE", typeSeance);
            resultIntent.putExtra("EXERCICES", selectedExercisesList.toArray(new String[0]));

            // Définir le résultat et terminer
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Séance ajoutée", Toast.LENGTH_SHORT).show();
            finish();
        });

        annulerButton.setOnClickListener(v -> finish());

        // Le reste du code de setupButtons reste inchangé...
        confirmExercisesButton.setOnClickListener(v -> {
            selectedExercisesList.clear();
            for (int i = 0; i < exercisesContainer.getChildCount(); i++) {
                View child = exercisesContainer.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) child;
                    if (checkBox.isChecked()) {
                        selectedExercisesList.add(checkBox.getText().toString());
                    }
                }
            }
            updateSelectedExercisesText();
            showExercisesViews(false);
        });

        cancelExercisesButton.setOnClickListener(v -> {
            // Désélectionner tous les exercices
            for (int i = 0; i < exercisesContainer.getChildCount(); i++) {
                View child = exercisesContainer.getChildAt(i);
                if (child instanceof CheckBox) {
                    ((CheckBox) child).setChecked(false);
                }
            }
            selectedExercisesList.clear();
            updateSelectedExercisesText();
            showExercisesViews(false);
        });

        // Rendre les textes d'exercices cliquables pour les modifier
        View.OnClickListener exerciseTextClickListener = v -> {
            String selectedType = ((Spinner) findViewById(R.id.seanceTypeSpinner)).getSelectedItem().toString();
            showExercisesViews(true);

            // Cocher les exercices déjà sélectionnés
            for (int i = 0; i < exercisesContainer.getChildCount(); i++) {
                View child = exercisesContainer.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) child;
                    checkBox.setChecked(selectedExercisesList.contains(checkBox.getText().toString()));
                }
            }
        };

        selectedExercisesText1.setOnClickListener(exerciseTextClickListener);
        selectedExercisesText2.setOnClickListener(exerciseTextClickListener);
    }

    private void updateSelectedExercisesText() {
        if (selectedExercisesList.isEmpty()) {
            selectedExercisesText1.setVisibility(View.GONE);
            selectedExercisesText2.setVisibility(View.GONE);
            return;
        }

        selectedExercisesText1.setVisibility(View.VISIBLE);

        // Diviser la liste si elle est trop longue
        if (selectedExercisesList.size() <= 3) {
            selectedExercisesText1.setText(String.join(", ", selectedExercisesList));
            selectedExercisesText2.setVisibility(View.GONE);
        } else {
            // Première moitié dans text1, seconde dans text2
            int middle = selectedExercisesList.size() / 2;
            List<String> firstHalf = selectedExercisesList.subList(0, middle);
            List<String> secondHalf = selectedExercisesList.subList(middle, selectedExercisesList.size());

            selectedExercisesText1.setText(String.join(", ", firstHalf));
            selectedExercisesText2.setText(String.join(", ", secondHalf));
            selectedExercisesText2.setVisibility(View.VISIBLE);
        }

        // Ajouter un texte indicatif pour informer que c'est cliquable
        if (!selectedExercisesList.isEmpty()) {
            selectedExercisesText1.setText(selectedExercisesText1.getText() + " (Cliquer pour modifier)");
        }
    }
    private void addExercises(LinearLayout container, String[] exercises) {
        // D'abord vider le conteneur pour éviter les doublons
        container.removeAllViews();

        for (String exercise : exercises) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(exercise);

            // Améliorer l'apparence et le toucher des checkboxes
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 12, 0, 12); // ajouter des marges pour rendre les cases plus grandes
            checkBox.setLayoutParams(params);

            // Augmenter la taille du texte pour une meilleure lisibilité
            checkBox.setTextSize(16);
            // Ajouter du padding pour agrandir la zone de toucher
            checkBox.setPadding(8, 16, 8, 16);

            container.addView(checkBox);
        }

        // Demander un rafraîchissement du layout
        container.requestLayout();
    }

    private void showExercisesViews(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        exercisesScrollView.setVisibility(visibility);
        exercisesHeader.setVisibility(visibility);
        exerciseButtonsLayout.setVisibility(visibility);

        if (show) {
            // Définir une hauteur minimale pour la ScrollView
            ViewGroup.LayoutParams params = exercisesScrollView.getLayoutParams();
            // Donner à la ScrollView une hauteur fixe pour s'assurer qu'elle prend assez d'espace
            params.height = 300; // hauteur en pixels (vous pouvez ajuster selon vos besoins)
            exercisesScrollView.setLayoutParams(params);

            // S'assurer que le LinearLayout à l'intérieur utilise wrap_content
            ViewGroup.LayoutParams containerParams = exercisesContainer.getLayoutParams();
            containerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            exercisesContainer.setLayoutParams(containerParams);
        }
    }

    @Override
    public void onClick(View view) {
        // Implémentation vide pour l'interface OnClickListener
    }
}