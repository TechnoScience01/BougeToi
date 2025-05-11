package com.example.bougetoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import com.example.bougetoi.databinding.ActivityMesSeancesBinding;

import java.util.Calendar;
import java.util.Date;

public class MesSeances extends AppCompatActivity implements View.OnClickListener {

    private ActivityMesSeancesBinding binding;
    private ActivityResultLauncher<Intent> ajoutSeanceLauncher;
    private ActivityResultLauncher<Intent> exercicesLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMesSeancesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.butajoutseance.setOnClickListener(this);
        binding.butexercices.setOnClickListener(this);
        binding.backArrow.setOnClickListener(this);

        // Enregistrer le launcher pour récupérer les résultats
        ajoutSeanceLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String nomSeance = data.getStringExtra("NOM_SEANCE");
                        String dateSeance = data.getStringExtra("DATE_SEANCE");
                        long dateTimestamp = data.getLongExtra("DATE_TIMESTAMP", 0);

                        // Sélectionner la date dans le calendrier
                        if (dateTimestamp > 0) {
                            binding.calendarView.setDate(dateTimestamp);
                        }

                        // Afficher un toast avec les informations de la séance
                        Toast.makeText(this,
                                "Séance " + nomSeance + " ajoutée pour le " + dateSeance,
                                Toast.LENGTH_SHORT).show();

                        // Ici, vous pouvez ajouter du code pour sauvegarder
                        // les informations de la séance dans une base de données
                    }
                }
        );

        exercicesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Vous pouvez gérer les résultats ici si nécessaire
                        Toast.makeText(this, "Activité Exercices terminée", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Configurer le CalendarView
        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);

            // Ici, vous pouvez ajouter du code pour charger
            // les séances prévues à cette date
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.butajoutseance) {
            // Lancer l'activité d'ajout de séance avec le launcher
            Intent intent = new Intent(MesSeances.this, Popup_ajout_seance.class);
            ajoutSeanceLauncher.launch(intent);
        } else if (id == R.id.butexercices) {
            Intent intent = new Intent(MesSeances.this, Exercices.class);
            exercicesLauncher.launch(intent);

        } else if (id == R.id.backArrow) {
            // Terminer cette activité pour revenir en arrière
            finish();
        }
    }
}