package org.foxesworld.animatix;

import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.*;

public class SplashScreenWindow extends JWindow {
    private AnimationFactory animationFactory;

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
        setSize(700, 360);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));
        animationFactory.createAnimation(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreenWindow splashScreen = new SplashScreenWindow();
            AnimationFrame.setDefaultFps(60);
            splashScreen.setVisible(true);
        });
    }
}