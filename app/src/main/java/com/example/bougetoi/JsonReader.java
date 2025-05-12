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