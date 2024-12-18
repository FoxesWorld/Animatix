package org.foxesworld.animatix;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        AnimationFactory animationFactory = new AnimationFactory("animatix.json5");

        // Настройка главного окна
        JFrame frame = new JFrame("Animation Demo");
        frame.setSize(600, 200);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создание панели для анимации
        JPanel animationPanel = new JPanel();
        animationPanel.setLayout(new BorderLayout());
        animationPanel.setBackground(Color.WHITE);

        // Создание панели для статуса анимации
        JPanel statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(800, 50));
        statusPanel.setBackground(new Color(0, 0, 0, 150));
        statusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel statusLabel = new JLabel("Animation running...");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusPanel.add(statusLabel);

        // Добавляем панели в главное окно
        frame.add(animationPanel, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Создание анимации
        animationFactory.createAnimation(animationPanel);

        // Отображение окна
        frame.setVisible(true);

        // Обновление статуса анимации
        Timer timer = new Timer(1000, e -> {
            if (animationFactory.isPaused()) {
                statusLabel.setText("Animation paused...");
            } else if (animationFactory.isRunning()) {
                statusLabel.setText("Animation running...");
            } else {
                statusLabel.setText("Animation stopped.");
            }
        });
        timer.start();
    }
}
