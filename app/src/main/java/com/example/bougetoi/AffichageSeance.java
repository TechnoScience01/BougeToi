package com.example.bougetoi;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AffichageSeance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_seance);

        // Retrieve the selected date from the intent
        Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("SELECTED_DATE");

        // Fetch the sessions for the selected date
        List<Seance> seances = getSeancesFromJson();
        List<Seance> filteredSeances = new ArrayList<>();
        for (Seance seance : seances) {
            if (seance.getDate().equals(selectedDate)) {
                filteredSeances.add(seance);
            }
        }

        // Display the sessions in a pop-up
        showSeancesPopup(filteredSeances, selectedDate);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

    }

    private void showSeancesPopup(List<Seance> seances, String date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Séances pour le " + date);

        if (seances.isEmpty()) {
            builder.setMessage("Aucune séance prévue pour cette date.");
        } else {
            StringBuilder seancesText = new StringBuilder();
            for (Seance seance : seances) {
                seancesText.append("- ").append(seance.getNom())
                        .append(": ").append(seance.getDescription()).append("\n");
            }
            builder.setMessage(seancesText.toString());
        }

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.create().show();
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
            // Return an empty list if the file doesn't exist or an error occurs
        }

        return seances;
    }
}