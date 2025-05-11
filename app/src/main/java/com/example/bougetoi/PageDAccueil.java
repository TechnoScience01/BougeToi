package com.example.bougetoi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PageDAccueil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_daccueil);

        // Initialisation des cartes
        CardView cardInfo = findViewById(R.id.cardInfo);
        CardView cardNutrition = findViewById(R.id.cardNutrition);
        CardView cardBodyTrack = findViewById(R.id.cardBodyTrack);
        CardView cardWorkouts = findViewById(R.id.cardWorkouts);

        // Configuration des listeners de clic
        cardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PageDAccueil.this, "Mes informations", Toast.LENGTH_SHORT).show();
                // Navigation à implémenter
                // Intent intent = new Intent(PageDAccueil.this, MesInformations.class);
                // startActivity(intent);
            }
        });

        cardNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PageDAccueil.this, "Mon suivi alimentaire", Toast.LENGTH_SHORT).show();
                // Navigation à implémenter
                // Intent intent = new Intent(PageDAccueil.this, SuiviAlimentaire.class);
                // startActivity(intent);
            }
        });

        cardBodyTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PageDAccueil.this, "Mon suivi morphologique", Toast.LENGTH_SHORT).show();
                // Navigation à implémenter
                // Intent intent = new Intent(PageDAccueil.this, SuiviMorphologique.class);
                // startActivity(intent);
            }
        });

        cardWorkouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PageDAccueil.this, "Mes séances", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PageDAccueil.this, MesSeances.class);
                startActivity(intent);
            }
        });

        // Animation des cartes à l'entrée
        animateCards(cardInfo, cardNutrition, cardBodyTrack, cardWorkouts);
    }

    private void animateCards(CardView card1, CardView card2, CardView card3, CardView card4) {
        // Définir l'état initial (hors écran)
        card1.setAlpha(0f);
        card2.setAlpha(0f);
        card3.setAlpha(0f);
        card4.setAlpha(0f);

        // Animation d'entrée en fondu
        card1.animate().alpha(1f).setDuration(300).setStartDelay(100);
        card2.animate().alpha(1f).setDuration(300).setStartDelay(200);
        card3.animate().alpha(1f).setDuration(300).setStartDelay(300);
        card4.animate().alpha(1f).setDuration(300).setStartDelay(400);
    }
}