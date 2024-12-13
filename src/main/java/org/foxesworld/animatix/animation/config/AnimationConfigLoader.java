package org.foxesworld.animatix.animation.config;

import com.google.gson.Gson;
import org.foxesworld.Main;

import java.io.InputStream;

public class AnimationConfigLoader {

    public AnimationConfig loadConfig(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        try (InputStream stream = inputStream) {
            Gson gson = new Gson();
            AnimationConfig config = gson.fromJson(new java.io.InputStreamReader(stream), AnimationConfig.class);
            Main.LOGGER.info("Loaded animation config: {}", config);
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load animation config from InputStream", e);
        }
    }
}
