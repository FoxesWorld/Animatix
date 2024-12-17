package org.foxesworld.animatix.animation.config;

import com.google.gson.Gson;
import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.Main;

import java.io.InputStream;

public class AnimationConfigLoader {

    public AnimationConfig loadConfig(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        try (InputStream stream = inputStream) {
            Gson gson = new Gson();
<<<<<<< Updated upstream
            AnimationConfig config = gson.fromJson(new java.io.InputStreamReader(stream), AnimationConfig.class);
            AnimationFactory.logger.info("Loaded animation config: {}", config);
=======
            AnimationConfig config = gson.fromJson(jsonObject, AnimationConfig.class);

            AnimationFactory.logger.log(System.Logger.Level.INFO, "Loaded animation config: {0}", config);
>>>>>>> Stashed changes
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load animation config from InputStream", e);
        }
    }
}
