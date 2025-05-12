package com.example.bougetoi;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.widget.ImageButton;

public class Exercices extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercices);

        LinearLayout container = findViewById(R.id.exercicesContainer);

        ImageButton backButton = findViewById(R.id.backArrow);
        backButton.setOnClickListener(v -> finish());

        addExerciseCard(container, R.drawable.curlbicepshalteres, "Exercice biceps : Curl haltères");
        addExerciseCard(container, R.drawable.curlbloquagecoude, "Exercice biceps : Curl coude bloqué");
        addExerciseCard(container, R.drawable.barre_au_front, "Exercice triceps : Barre au front");
        addExerciseCard(container, R.drawable.dipsbarresparalleles, "Exercice pectoraux-triceps : Dips");
        addExerciseCard(container, R.drawable.ecartepoulie, "Exercice pectoraux : Ecarté poulie");
        addExerciseCard(container, R.drawable.devmilitaire, "Exercice epaule : Développé militaire");
        addExerciseCard(container, R.drawable.soulevedeterre, "Exercice dos : Soulevé de terre");
        addExerciseCard(container, R.drawable.tiragevertical, "Exercice dos : Tirage vertical");
        addExerciseCard(container, R.drawable.tractionsupination, "Exercice dos : Tractions supination");
        addExerciseCard(container, R.drawable.presseajambe, "Exercice cuisse : Presse à jambes");
        addExerciseCard(container, R.drawable.squat, "Exercice cuisse : Squat");
        addExerciseCard(container, R.drawable.fentesavant, "Exercice cuisse : Fentes avant");
        addExerciseCard(container, R.drawable.squatsumo, "Exercice cuisse : Squat sumo");
        addExerciseCard(container, R.drawable.elevation_mollet, "Exercice mollet : Pointe de pied machine");
        addExerciseCard(container, R.drawable.molletpresse, "Exercice mollet : Mollet presse");
        addExerciseCard(container, R.drawable.hiptrust, "Exercice fessiers : Hip thrust");
        addExerciseCard(container, R.drawable.legcurlallonge, "Exercice ischios : Leg curl allongé");
        addExerciseCard(container, R.drawable.planche, "Exercice abdos : Planche");
    }

    private void addExerciseCard(LinearLayout container, int imageResId, String text) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16); // Marge en bas pour espacer les cartes
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(4f);
        cardView.setRadius(8f);

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardContent.setOrientation(LinearLayout.HORIZONTAL);
        cardContent.setPadding(16, 16, 16, 16);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(150, 150);
        imageView.setLayoutParams(imageParams);
        imageView.setImageResource(imageResId);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cardContent.addView(imageView);

        TextView textView = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.setMarginStart(16);
        textView.setLayoutParams(textParams);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        cardContent.addView(textView);

        cardView.addView(cardContent);

        container.addView(cardView);

        cardView.setClickable(true);
        cardView.setFocusable(true);

        int[] attrs = new int[] { android.R.attr.selectableItemBackground };
        android.content.res.TypedArray typedArray = obtainStyledAttributes(attrs);
        cardView.setForeground(typedArray.getDrawable(0));
        typedArray.recycle();

        cardView.setOnClickListener(v -> {
        });
    }
}