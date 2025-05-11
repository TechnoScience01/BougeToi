package com.example.bougetoi;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class JsonReader {
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
}
