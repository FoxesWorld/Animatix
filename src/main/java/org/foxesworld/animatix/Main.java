package org.foxesworld.animatix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class Main {
    public static Logger LOGGER;

    public static void main(String[] args) {
        System.setProperty("log.dir", System.getProperty("user.dir"));
        System.setProperty("log.level", "DEBUG");
        LOGGER = LogManager.getLogger(Main.class);

            AnimationFactory animationFactory = new AnimationFactory("animatix.json");

            JFrame frame = new JFrame("Animation Demo");
            frame.setSize(400, 300);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            animationFactory.createAnimation(frame);
    }
}
