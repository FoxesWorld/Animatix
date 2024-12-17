package org.foxesworld.animatix.animation.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.foxesworld.animatix.AnimationFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

public class AnimationConfigLoader {

    public AnimationConfig loadConfig(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            JsonReader json5Reader = new JsonReader(reader);
            JsonObject jsonObject = JsonParser.parseReader(json5Reader).getAsJsonObject();

            // Десериализуем JSON5 в объект AnimationConfig с помощью Gson
            Gson gson = new Gson();
            AnimationConfig config = gson.fromJson(jsonObject, AnimationConfig.class);

            AnimationFactory.logger.log(System.Logger.Level.INFO, "Loaded animation config: {}", config);
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load animation config from InputStream", e);
        }
    }
}
