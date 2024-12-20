package org.foxesworld.animatix;

import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.*;

public class SplashScreenWindow extends JWindow {
    private AnimationFactory animationFactory;

    public SplashScreenWindow() {


        // Создаем панель для анимации
        JPanel content = new JPanel(null) {
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
        setSize(700, 560);
        setLocationRelativeTo(null);
        //setBackground(Color.CYAN);
        animationFactory = new AnimationFactory("animatix.json5", content);
        animationFactory.createAnimation();  // Передаем панель 'content'
    }

    public static void main(String[] args) {
        System.setProperty("System.tracers", "true");
        SwingUtilities.invokeLater(() -> {
            SplashScreenWindow splashScreen = new SplashScreenWindow();
            AnimationFrame.setDefaultFps(60);
            splashScreen.setVisible(true);
        });
    }
}
