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

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        chosenDate = calendar.getTime();

        initializeViews();
        setupSpinner();
        setupButtons();
        setupDatePicker();
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.seanceTypeSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.seance_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();

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
        selectedDate.setText(dateFormat.format(calendar.getTime()));

        selectedDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        chosenDate = calendar.getTime();

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
            String nomSeance = ((EditText) findViewById(R.id.nomSeance)).getText().toString();
            String dureeStr = ((EditText) findViewById(R.id.dureeSeance)).getText().toString();
            String description = ((EditText) findViewById(R.id.descriptionSeance)).getText().toString();
            String typeSeance = ((Spinner) findViewById(R.id.seanceTypeSpinner)).getSelectedItem().toString();

            if (nomSeance.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir un nom pour la séance", Toast.LENGTH_SHORT).show();
                return;
            }

            Seance seance = new Seance(
                    nomSeance,
                    dateFormat.format(chosenDate),
                    dureeStr.isEmpty() ? 0 : Integer.parseInt(dureeStr),
                    description,
                    typeSeance,
                    selectedExercisesList
            );

            JsonReader.saveSeanceToJson(this, seance);

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

            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Séance ajoutée", Toast.LENGTH_SHORT).show();
            finish();
        });

        annulerButton.setOnClickListener(v -> finish());

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

        View.OnClickListener exerciseTextClickListener = v -> {
            String selectedType = ((Spinner) findViewById(R.id.seanceTypeSpinner)).getSelectedItem().toString();
            showExercisesViews(true);

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

        if (selectedExercisesList.size() <= 3) {
            selectedExercisesText1.setText(String.join(", ", selectedExercisesList));
            selectedExercisesText2.setVisibility(View.GONE);
        } else {
            int middle = selectedExercisesList.size() / 2;
            List<String> firstHalf = selectedExercisesList.subList(0, middle);
            List<String> secondHalf = selectedExercisesList.subList(middle, selectedExercisesList.size());

            selectedExercisesText1.setText(String.join(", ", firstHalf));
            selectedExercisesText2.setText(String.join(", ", secondHalf));
            selectedExercisesText2.setVisibility(View.VISIBLE);
        }

        if (!selectedExercisesList.isEmpty()) {
            selectedExercisesText1.setText(selectedExercisesText1.getText() + " (Cliquer pour modifier)");
        }
    }

    private void addExercises(LinearLayout container, String[] exercises) {
        container.removeAllViews();

        for (String exercise : exercises) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(exercise);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 12, 0, 12); // ajouter des marges pour rendre les cases plus grandes
            checkBox.setLayoutParams(params);

            checkBox.setTextSize(16);
            checkBox.setPadding(8, 16, 8, 16);

            container.addView(checkBox);
        }

        container.requestLayout();
    }

    private void showExercisesViews(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        exercisesScrollView.setVisibility(visibility);
        exercisesHeader.setVisibility(visibility);
        exerciseButtonsLayout.setVisibility(visibility);

        if (show) {
            ViewGroup.LayoutParams params = exercisesScrollView.getLayoutParams();
            params.height = 300;
            exercisesScrollView.setLayoutParams(params);

            ViewGroup.LayoutParams containerParams = exercisesContainer.getLayoutParams();
            containerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            exercisesContainer.setLayoutParams(containerParams);
        }
    }

    @Override
    public void onClick(View view) {
    }
}