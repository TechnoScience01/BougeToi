package com.example.bougetoi;

import android.content.Context;
import android.util.Log;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = readJsonFile(context);

        JsonObject jsonObject = getJsonObject(gson, jsonString);
        JsonArray seancesArray = jsonObject.has("seances") ? jsonObject.getAsJsonArray("seances") : new JsonArray();
        seancesArray.add(gson.toJsonTree(seance));
        jsonObject.add("seances", seancesArray);

        writeJsonFile(context, gson.toJson(jsonObject));
    }

    public static void saveInfoToJson(Context context, UserInfo userInfo) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = readJsonFile(context);

        JsonObject jsonObject = getJsonObject(gson, jsonString);
        jsonObject.add("infoUser", gson.toJsonTree(userInfo));

        writeJsonFile(context, gson.toJson(jsonObject));
    }
    public static float getObjectifPoids(Context context) {
        String jsonString = readJsonFile(context);
        try {
            JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
            if (jsonObject != null && jsonObject.has("infoUser")) {
                JsonObject infoUser = jsonObject.getAsJsonObject("infoUser");
                if (infoUser.has("objectif_poids")) {
                    JsonElement element = infoUser.get("objectif_poids");

                    if (element.isJsonPrimitive()) {
                        JsonPrimitive primitive = element.getAsJsonPrimitive();
                        if (primitive.isNumber()) {
                            return primitive.getAsFloat();  // direct si c’est un nombre
                        } else if (primitive.isString()) {
                            try {
                                return Float.parseFloat(primitive.getAsString()); // tente de parser la chaîne
                            } catch (NumberFormatException e) {
                                Log.w("JsonReader", "objectif_poids en string mais invalide : " + primitive.getAsString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("JsonReader", "Erreur lors de la lecture de l'objectif poids", e);
        }
        return 70f;
    }

    public static float getTaille(Context context) {
        String jsonString = readJsonFile(context);
        try {
            JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
            if (jsonObject != null && jsonObject.has("infoUser")) {
                JsonObject infoUser = jsonObject.getAsJsonObject("infoUser");
                if (infoUser.has("taille")) {
                    return infoUser.get("taille").getAsFloat();
                }
            }
        } catch (Exception e) {
            Log.e("JsonReader", "Erreur lors de la lecture de la taille", e);
        }
        return 0f;
    }

    public static void pushHumeur(Context context, String nouvelleHumeur) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = readJsonFile(context);

        JsonObject jsonObject = getJsonObject(gson, jsonString);
        JsonArray humeurArray = jsonObject.has("humeurs") ? jsonObject.getAsJsonArray("humeurs") : new JsonArray();
        JsonArray nouveauArray = new JsonArray();
        nouveauArray.add(nouvelleHumeur);

        for (int i = 0; i < humeurArray.size() && i < 29; i++) {
            nouveauArray.add(humeurArray.get(i));
        }

        jsonObject.add("humeurs", nouveauArray);
        writeJsonFile(context, gson.toJson(jsonObject));
    }

    public static String getDerniereHumeur(Context context) {
        Gson gson = new Gson();
        String jsonString = readJsonFile(context);

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if (jsonObject != null && jsonObject.has("humeurs")) {
            JsonArray humeurArray = jsonObject.getAsJsonArray("humeurs");
            if (humeurArray.size() > 0) {
                return humeurArray.get(0).getAsString();
            }
        }
        return null;
    }

    public static Float getDernierPoids(Context context) {
        Gson gson = new Gson();
        String jsonString = readJsonFile(context);

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if (jsonObject != null && jsonObject.has("poids")) {
            JsonArray poidsArray = jsonObject.getAsJsonArray("poids");
            if (poidsArray.size() > 0) {
                return poidsArray.get(0).getAsFloat();
            }
        }
        return null;
    }

    public static List<Float> getPoidsFromJson(Context context) {
        Gson gson = new Gson();
        List<Float> poidsList = new ArrayList<>();
        String jsonString = readJsonFile(context);

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if (jsonObject != null && jsonObject.has("poids")) {
            JsonArray poidsArray = jsonObject.getAsJsonArray("poids");
            for (JsonElement element : poidsArray) {
                poidsList.add(element.getAsFloat());
            }
        }

        while (poidsList.size() < 30) {
            poidsList.add(0f);
        }

        return poidsList;
    }

    public static void pushPoids(Context context, float nouveauPoids) {
        List<Float> poidsList = getPoidsFromJson(context);
        poidsList.add(0, nouveauPoids);

        if (poidsList.size() > 30) {
            poidsList = poidsList.subList(0, 30);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = readJsonFile(context); // <-- lire l'existant
        JsonObject jsonObject = getJsonObject(gson, jsonString);

        JsonArray poidsArray = new JsonArray();
        for (Float poids : poidsList) {
            poidsArray.add(poids);
        }

        jsonObject.add("poids", poidsArray); // <-- mettre à jour, pas écraser

        writeJsonFile(context, gson.toJson(jsonObject));
    }


    public static MorphoData convertJsonToObject(Context context) {
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.bougetoidata)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String jsonString = new String(data, "UTF-8");
            return new Gson().fromJson(jsonString, new TypeToken<MorphoData>() {}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static UserInfo loadInfoFromJson(Context context) {
        Gson gson = new Gson();
        String jsonString = readJsonFile(context);

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if (jsonObject != null && jsonObject.has("infoUser")) {
            return gson.fromJson(jsonObject.getAsJsonObject("infoUser"), UserInfo.class);
        }
        return null;
    }

    public static boolean hasKey(Context context, String key) {
        String jsonString = readJsonFile(context);
        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        return jsonObject != null && jsonObject.has(key);
    }

    private static String readJsonFile(Context context) {
        try (InputStream inputStream = context.openFileInput(FILE_NAME)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return new String(data, "UTF-8");
        } catch (IOException e) {
            return "{}";
        }
    }

    private static void writeJsonFile(Context context, String jsonString) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(jsonString);
        } catch (IOException e) {
            Log.e("JsonReader", "Erreur d’écriture JSON", e);
        }
    }

    private static JsonObject getJsonObject(Gson gson, String jsonString) {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        return (jsonObject != null) ? jsonObject : new JsonObject();
    }
    public static void saveSuiviToJson(Context context, MonSuiviAlimentaire.SuiviJournalier suivi) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = "";
        String FILE_NAME = "bougetoidata.json";

        jsonString = readFileFromContext(context, FILE_NAME);

        JsonObject rootObject = gson.fromJson(jsonString, JsonObject.class);
        if (rootObject == null) {
            rootObject = new JsonObject();
        }

        JsonArray suivisArray = rootObject.has("suivis") ? rootObject.getAsJsonArray("suivis") : new JsonArray();

        boolean found = false;
        for (JsonElement element : suivisArray) {
            JsonObject obj = element.getAsJsonObject();
            String date = obj.get("date").getAsString();

            if (date.equals(suivi.date)) {
                obj.add("aliments", gson.toJsonTree(suivi.aliments));  // Ajouter/mettre à jour les aliments
                found = true;
                break;
            }
        }

        if (!found) {
            JsonElement suiviJson = gson.toJsonTree(suivi);
            suivisArray.add(suiviJson);
        }

        rootObject.add("suivis", suivisArray);

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
        String jsonString = "{}";

        try (InputStream inputStream = context.openFileInput(fileName)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            jsonString = new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
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

    public static float getObjectifCalorique(Context context) {
        String jsonString = readJsonFile(context);
        try {
            JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
            if (jsonObject != null && jsonObject.has("infoUser")) {
                JsonObject infoUser = jsonObject.getAsJsonObject("infoUser");
                if (infoUser.has("objectif_calorique")) {
                    JsonElement element = infoUser.get("objectif_calorique");

                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                        return element.getAsFloat();  // Renvoie directement la valeur si c'est un nombre
                    }
                }
            }
        } catch (Exception e) {
            Log.e("JsonReader", "Erreur lors de la lecture de l'objectif calorique", e);
        }
        return 0;
    }




}
