package com.example.bougetoi;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MesInformations extends AppCompatActivity implements View.OnClickListener {

    private EditText firstNameInput, lastNameInput, dateOfBirthInput, genderInput, objectiveInput, performanceGoalInput, objPoids, heightInput, caloInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_informations);

        // Initialize input fields
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        dateOfBirthInput = findViewById(R.id.dateOfBirthInput);
        genderInput = findViewById(R.id.genderInput);
        objectiveInput = findViewById(R.id.objectiveInput);
        performanceGoalInput = findViewById(R.id.performanceGoalInput);
        objPoids = findViewById(R.id.ObjPoids);
        heightInput = findViewById(R.id.heightInput);
        caloInput = findViewById(R.id.calo);

        // Load user info from JSON
        if (JsonReader.hasKey(this, "infoUser")) {
            showInfoFromJson();
        } else {
            clearInputFields();
        }

        // Set up the back arrow button
        findViewById(R.id.backArrow).setOnClickListener(v -> finish());

        findViewById(R.id.saveButton).setOnClickListener(v -> saveUserInfo());
    }

    private void clearInputFields() {
        firstNameInput.setText("");
        lastNameInput.setText("");
        dateOfBirthInput.setText("");
        genderInput.setText("");
        objectiveInput.setText("");
        performanceGoalInput.setText("");
        objPoids.setText("");
        heightInput.setText("");
        caloInput.setText("");
    }
    private void saveUserInfo() {
        // Collect data from input fields
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String dateOfBirth = dateOfBirthInput.getText().toString();
        String gender = genderInput.getText().toString();
        String objective = objectiveInput.getText().toString();
        String performanceGoal = performanceGoalInput.getText().toString();
        String objPoidsS = objPoids.getText().toString();
        String heightStr = heightInput.getText().toString();
        String caloStr = caloInput.getText().toString();


        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || dateOfBirth.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse numeric fields
        float height = heightStr.isEmpty() ? 0 : Float.parseFloat(heightStr);
        float calo = caloStr.isEmpty() ? 0 : Float.parseFloat(caloStr);

        // Create UserInfo object
        UserInfo userInfo = new UserInfo();
        userInfo.setPrenom(firstName);
        userInfo.setNom(lastName);
        userInfo.setDateNaissance(dateOfBirth);
        userInfo.setGenre(gender);
        userInfo.setObjectif(objective);
        userInfo.setTaille(height);
        userInfo.setObjectif_calorique(calo);
        userInfo.setObjectif_poids(objPoidsS);
        userInfo.setObjectif_performance(performanceGoal);

        // Save to JSON
        JsonReader.saveInfoToJson(this, userInfo);
        Toast.makeText(this, "Informations sauvegardées avec succès", Toast.LENGTH_SHORT).show();
    }

    public void showInfoFromJson() {
        UserInfo userInfo = JsonReader.loadInfoFromJson(this);

        if (userInfo != null) {
            firstNameInput.setText(userInfo.getPrenom());
            lastNameInput.setText(userInfo.getNom());
            dateOfBirthInput.setText(userInfo.getDateNaissance());
            genderInput.setText(userInfo.getGenre());
            objectiveInput.setText(userInfo.getObjectif());
            performanceGoalInput.setText(userInfo.getObjectif_performance());
            objPoids.setText(userInfo.getObjectif_poids());
            heightInput.setText(String.valueOf(userInfo.getTaille()));
            caloInput.setText(String.valueOf(userInfo.getObjectif_calorique()));
        } else {
            Toast.makeText(this, "Aucune information utilisateur trouvée", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backArrow) {
            finish();
        }
    }

}