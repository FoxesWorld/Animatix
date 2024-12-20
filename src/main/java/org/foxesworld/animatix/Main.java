package org.foxesworld.animatix;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {


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
        AnimationFactory animationFactory = new AnimationFactory("animatix.json5", animationPanel);
        animationFactory.createAnimation();

        // Отображение окна
        frame.setVisible(true);
    }
}
