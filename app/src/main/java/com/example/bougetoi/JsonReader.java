package com.example.bougetoi;

import android.content.Context;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.example.bougetoi.Seance;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonReader {
    private static final String FILE_NAME = "bougetoidata.json";

    public static void saveSeanceToJson(Context context, Seance seance) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Activer le Pretty Printing
        String fileName = "bougetoidata.json";

        // Charger les données existantes
        String jsonString = "";
        try (InputStream inputStream = context.openFileInput(fileName)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            // Si le fichier n'existe pas, on initialise un JSON vide
            jsonString = "{}";
        }

        // Convertir le JSON en objet modifiable
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }

        // Récupérer ou créer la liste des séances
        JsonArray seancesArray = jsonObject.has("seances") ? jsonObject.getAsJsonArray("seances") : new JsonArray();

        // Ajouter la nouvelle séance
        JsonElement seanceJson = gson.toJsonTree(seance);
        seancesArray.add(seanceJson);

        // Mettre à jour l'objet JSON
        jsonObject.add("seances", seancesArray);

        // Écrire dans le fichier avec une bonne organisation
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(gson.toJson(jsonObject)); // Écriture formatée
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'écriture du fichier JSON", e);
        }
    }
    public static List<Seance> getSeancesFromJson(Context context) {
        Gson gson = new Gson();
        List<Seance> seances = new ArrayList<>();

        try (InputStream inputStream = context.openFileInput(FILE_NAME)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String jsonString = new String(data, "UTF-8");

            seances = gson.fromJson(jsonString, new TypeToken<List<Seance>>() {}.getType());
        } catch (Exception e) {
            // Retourne une liste vide si le fichier n'existe pas ou en cas d'erreur
        }

        return seances;
    }
    public static morphodata convertJsonToObject(Context context ) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.bougetoidata);

        String jsonString ="";
        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Gson().fromJson(jsonString, new TypeToken<morphodata>(){}.getType());
    }
    public static void saveSuiviToJson(Context context, MonSuiviAlimentaire.SuiviJournalier suivi) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = "";
        String FILE_NAME = "bougetoidata.json";

        // Charger le contenu du fichier JSON existant
        jsonString = readFileFromContext(context, FILE_NAME);

        // Convertir le JSON en objet modifiable
        JsonObject rootObject = gson.fromJson(jsonString, JsonObject.class);
        if (rootObject == null) {
            rootObject = new JsonObject();
        }

        // Vérifie ou crée l'objet "suivis"
        JsonArray suivisArray = rootObject.has("suivis") ? rootObject.getAsJsonArray("suivis") : new JsonArray();

        // Vérifier si un suivi pour aujourd'hui existe déjà
        boolean found = false;
        for (JsonElement element : suivisArray) {
            JsonObject obj = element.getAsJsonObject();
            String date = obj.get("date").getAsString();

            // Si la date correspond à aujourd'hui, on met à jour le suivi
            if (date.equals(suivi.date)) {
                obj.add("aliments", gson.toJsonTree(suivi.aliments));  // Ajouter/mettre à jour les aliments
                found = true;
                break;
            }
        }

        // Si le suivi pour aujourd'hui n'a pas été trouvé, on l'ajoute à la liste
        if (!found) {
            JsonElement suiviJson = gson.toJsonTree(suivi);
            suivisArray.add(suiviJson);
        }

        // Mettre à jour l'objet JSON avec le tableau "suivis"
        rootObject.add("suivis", suivisArray);

        // Sauvegarder le fichier avec les nouvelles données
        writeFileToContext(context, FILE_NAME, gson.toJson(rootObject));
    }

    public static List<MonSuiviAlimentaire.SuiviJournalier> getSuivisFromJson(Context context) {
        Gson gson = new Gson();
        List<MonSuiviAlimentaire.SuiviJournalier> suivis = new ArrayList<>();

        String jsonString = "";
        try (InputStream inputStream = context.openFileInput(FILE_NAME)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            jsonString = new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Retourne liste vide si fichier non trouvé
            return suivis;
        }

        JsonObject rootObject = gson.fromJson(jsonString, JsonObject.class);
        if (rootObject != null && rootObject.has("suivis")) {
            JsonArray suivisArray = rootObject.getAsJsonArray("suivis");
            for (JsonElement element : suivisArray) {
                MonSuiviAlimentaire.SuiviJournalier suivi = gson.fromJson(element, MonSuiviAlimentaire.SuiviJournalier.class);
                suivis.add(suivi);
            }
        }

        return suivis;
    }
    private static String readFileFromContext(Context context, String fileName) {
        String jsonString = "{}";  // Initialisation par défaut

        try (InputStream inputStream = context.openFileInput(fileName)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            jsonString = new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();  // Pour débogage
        }

        return jsonString;
    }
    private static void writeFileToContext(Context context, String fileName, String jsonData) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            writer.write(jsonData);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'écriture du fichier " + fileName, e);
        }
    }



}