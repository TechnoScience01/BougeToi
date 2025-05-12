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

    public static void saveInfoToJson(Context context, UserInfo userInfo) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String fileName = "bougetoidata.json";

        String jsonString = "";
        try (InputStream inputStream = context.openFileInput(fileName)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            jsonString = "{}";
        }

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }

        JsonElement userInfoJson = gson.toJsonTree(userInfo);
        jsonObject.add("infoUser", userInfoJson);

        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(gson.toJson(jsonObject));
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'écriture du fichier JSON", e);
        }
    }


        public static morphodata convertJsonToObject(Context context ) {

    public static void pushHumeur(Context context, String nouvelleHumeur) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String fileName = "bougetoidata.json";

        String jsonString = "";
        try (InputStream inputStream = context.openFileInput(fileName)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            jsonString = "{}"; // fichier vide
        }

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }

        JsonArray humeurArray = jsonObject.has("humeurs") ? jsonObject.getAsJsonArray("humeurs") : new JsonArray();

        // Crée un nouveau tableau avec la nouvelle humeur en tête
        JsonArray nouveauArray = new JsonArray();
        nouveauArray.add(nouvelleHumeur);
        for (int i = 0; i < humeurArray.size() && i < 29; i++) {
            nouveauArray.add(humeurArray.get(i));
        }

        jsonObject.add("humeurs", nouveauArray);

        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(gson.toJson(jsonObject));
        } catch (IOException e) {
            Log.e("JsonReader", "Erreur d’écriture JSON dans pushHumeur", e);
        }
    }

    public static String getDerniereHumeur(Context context) {
        Gson gson = new Gson();
        String fileName = "bougetoidata.json";

        try (InputStream inputStream = context.openFileInput(fileName)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String jsonString = new String(data, "UTF-8");

            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            if (jsonObject != null && jsonObject.has("humeurs")) {
                JsonArray humeurArray = jsonObject.getAsJsonArray("humeurs");
                if (humeurArray.size() > 0) {
                    return humeurArray.get(0).getAsString();
                }
            }
        } catch (IOException e) {
            Log.w("JsonReader", "Impossible de lire l'humeur : " + e.getMessage());
        }

        return null;
    }

    public static Float getDernierPoids(Context context) {
        Gson gson = new Gson();
        String fileName = "bougetoidata.json";

        try (InputStream inputStream = context.openFileInput(fileName)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String jsonString = new String(data, "UTF-8");

            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            if (jsonObject != null && jsonObject.has("poids")) {
                JsonArray poidsArray = jsonObject.getAsJsonArray("poids");
                if (poidsArray.size() > 0) {
                    return poidsArray.get(0).getAsFloat(); // le plus récent
                }
            }
        } catch (IOException e) {
            Log.w("JsonReader", "Erreur de lecture du dernier poids : " + e.getMessage());
        }

        return null; // ou -1f si tu préfères une valeur par défaut
    }


    public static List<Float> getPoidsFromJson(Context context) {
        Gson gson = new Gson();
        List<Float> poidsList = new ArrayList<>();
        String fileName = "bougetoidata.json";

        try (InputStream inputStream = context.openFileInput(fileName)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String jsonString = new String(data, "UTF-8");

            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            if (jsonObject != null && jsonObject.has("poids")) {
                JsonArray poidsArray = jsonObject.getAsJsonArray("poids");
                for (JsonElement element : poidsArray) {
                    poidsList.add(element.getAsFloat());
                }
            }
        } catch (IOException e) {
            Log.w("JsonReader", "Fichier JSON inexistant ou vide. Retour fallback.");
        }

        if (poidsList.isEmpty()) {
            for (int i = 0; i < 30; i++) {
                poidsList.add(0f);
            }
        }

        return poidsList;
    }
    public static void pushPoids(Context context, float nouveauPoids) {
        List<Float> poidsList = getPoidsFromJson(context);

        // Ajouter en début de liste
        poidsList.add(0, nouveauPoids);

        // Limiter à 30 éléments
        if (poidsList.size() > 30) {
            poidsList = poidsList.subList(0, 30);
        }

        // Réécriture dans le fichier JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();

        JsonArray poidsArray = new JsonArray();
        for (Float poids : poidsList) {
            poidsArray.add(poids);
        }

        jsonObject.add("poids", poidsArray);

        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(gson.toJson(jsonObject));
        } catch (IOException e) {
            Log.e("JsonReader", "Erreur d’écriture JSON dans pushPoids", e);
        }
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

    public static UserInfo loadInfoFromJson(Context context) {
        Gson gson = new Gson();
        UserInfo userInfo = null;

        try (InputStream inputStream = context.openFileInput("bougetoidata.json")) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String jsonString = new String(data, "UTF-8");

            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            if (jsonObject != null && jsonObject.has("infoUser")) {
                JsonObject infoUserObject = jsonObject.getAsJsonObject("infoUser");
                userInfo = gson.fromJson(infoUserObject, UserInfo.class);
            }
        } catch (Exception e) {
            // Return null if the file doesn't exist or an error occurs
            return null;
        }
        return userInfo;
    }

    public static boolean hasKey(MesInformations mesInformations, String infoUser) {
        try (InputStream inputStream = mesInformations.openFileInput("bougetoidata.json")) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String jsonString = new String(data, "UTF-8");

            JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
            return jsonObject != null && jsonObject.has(infoUser);
        } catch (IOException e) {
            return false; // Si le fichier n'existe pas, on retourne false
        }
    }
}
