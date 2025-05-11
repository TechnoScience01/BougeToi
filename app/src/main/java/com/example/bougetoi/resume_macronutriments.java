package com.example.bougetoi;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class resume_macronutriments extends AppCompatActivity {

    private String[] aliments;
    private double[] proteinesParGramme;
    private double[] glucidesParGramme;
    private double[] lipidesParGramme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_macronutriments);

        // Récupérer les données transmises
        String alimentsStr = getIntent().getStringExtra("aliments");
        String proteinesStr = getIntent().getStringExtra("proteines");
        String glucidesStr = getIntent().getStringExtra("glucides");
        String lipidesStr = getIntent().getStringExtra("lipides");

        aliments = (alimentsStr != null) ? alimentsStr.split(",") : new String[0];
        proteinesParGramme = (proteinesStr != null) ? convertToDoubleArray(proteinesStr.split(",")) : new double[0];
        glucidesParGramme = (glucidesStr != null) ? convertToDoubleArray(glucidesStr.split(",")) : new double[0];
        lipidesParGramme = (lipidesStr != null) ? convertToDoubleArray(lipidesStr.split(",")) : new double[0];

        // Ajouter les données au tableau
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        for (int i = 0; i < aliments.length; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            );
            row.setLayoutParams(params);

            TextView alimentTextView = new TextView(this);
            alimentTextView.setText(aliments[i]);
            row.addView(alimentTextView);

            TextView proteinesTextView = new TextView(this);
            proteinesTextView.setText(String.format("%.2f", proteinesParGramme[i]));
            row.addView(proteinesTextView);

            TextView glucidesTextView = new TextView(this);
            glucidesTextView.setText(String.format("%.2f", glucidesParGramme[i]));
            row.addView(glucidesTextView);

            TextView lipidesTextView = new TextView(this);
            lipidesTextView.setText(String.format("%.2f", lipidesParGramme[i]));
            row.addView(lipidesTextView);

            tableLayout.addView(row);
        }
    }

    private double[] convertToDoubleArray(String[] stringArray) {
        double[] doubleArray = new double[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            doubleArray[i] = Double.parseDouble(stringArray[i]);
        }
        return doubleArray;
    }
}