package org.foxesworld;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.animatix.AnimationFactory;

import javax.swing.*;
import java.io.InputStream;

public class Main {
    public static Logger LOGGER;

    public static void main(String[] args) {
        System.setProperty("log.dir", System.getProperty("user.dir"));
        System.setProperty("log.level", "DEBUG");
        LOGGER = LogManager.getLogger(Main.class);

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("animatix.json")) {
            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found!");
            }

            // Загрузка конфигурации
            AnimationFactory animationFactory = new AnimationFactory();
            animationFactory.loadConfig(inputStream);

            // Создание окна
            JFrame frame = new JFrame("Animation Demo");
            frame.setSize(400, 300);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            // Создание анимации
            animationFactory.createAnimation(frame);

        } catch (Exception e) {
            LOGGER.error("Failed to initialize animation factory", e);
        }
    }
}
