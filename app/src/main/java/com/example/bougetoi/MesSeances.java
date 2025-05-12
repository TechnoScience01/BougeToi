package com.example.bougetoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bougetoi.databinding.ActivityMesSeancesBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
                    }
                }
        );

        exercicesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Activité Exercices terminée", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Configurer le CalendarView
        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Formater la date sélectionnée
            String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);

            // Lancer l'activité AffichageSeance avec la date sélectionnée
            Intent intent = new Intent(MesSeances.this, AffichageSeance.class);
            intent.putExtra("SELECTED_DATE", selectedDate);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.butajoutseance) {
            Intent intent = new Intent(MesSeances.this, Popup_ajout_seance.class);
            ajoutSeanceLauncher.launch(intent);
        } else if (id == R.id.butexercices) {
            Intent intent = new Intent(MesSeances.this, Exercices.class);
            exercicesLauncher.launch(intent);
        } else if (id == R.id.backArrow) {
            finish();
        }
    }

    private List<Seance> getSeancesFromJson() {
        Gson gson = new Gson();
        List<Seance> seances = new ArrayList<>();

        try (InputStream inputStream = openFileInput("bougetoidata.json")) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String jsonString = new String(data, "UTF-8");

            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            if (jsonObject != null && jsonObject.has("seances")) {
                JsonArray seancesArray = jsonObject.getAsJsonArray("seances");
                seances = gson.fromJson(seancesArray, new TypeToken<List<Seance>>() {}.getType());
            }
        } catch (Exception e) {
            // Retourne une liste vide si le fichier n'existe pas ou en cas d'erreur
        }

        return seances;
    }
}