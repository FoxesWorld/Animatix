package org.foxesworld.animatix;

import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.*;

public class SplashScreenWindow extends JWindow {
    private final AnimationFactory animationFactory;

    public SplashScreenWindow() {
        animationFactory = new AnimationFactory("animatix.json5");

        SwingUtilities.invokeLater(() -> {

        });

        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }

            @Override
            public void setOpaque(boolean isOpaque) {
                super.setOpaque(false);

            }
        };


        getContentPane().add(content);
        setSize(600, 360);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));
        animationFactory.createAnimation(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreenWindow splashScreen = new SplashScreenWindow();
            AnimationFrame.setDefaultFps(144);
            splashScreen.setVisible(true);
        });
    }
}