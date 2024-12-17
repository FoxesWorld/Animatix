package org.foxesworld.animatix;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

            AnimationFactory animationFactory = new AnimationFactory("animatix.json");

            JFrame frame = new JFrame("Animation Demo");
            frame.setSize(400, 300);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

<<<<<<< Updated upstream
            animationFactory.createAnimation(frame);
=======
        // Кнопка Старт
        JButton startButton = new JButton("Start");
        startButton.setBounds(50, 20, 100, 40);
        startButton.addActionListener(e -> {
            animationFactory.getAnimPhase().resume();
            JOptionPane.showMessageDialog(frame, "Animation started");
        });

        // Кнопка Пауза
        JButton pauseButton = new JButton("Pause");
        pauseButton.setBounds(200, 20, 100, 40);
        pauseButton.addActionListener(e -> {
            animationFactory.getAnimPhase().pause();
            JOptionPane.showMessageDialog(frame, "Animation paused");
        });

        // Кнопка Рестарт
        JButton restartButton = new JButton("Restart");
        restartButton.setBounds(350, 20, 100, 40);
        restartButton.addActionListener(e -> {
            animationFactory.dispose(); // Остановка текущей анимации
            animationFactory.createAnimation(frame); // Перезапуск
            JOptionPane.showMessageDialog(frame, "Animation restarted");
        });

        // Добавляем кнопки в панель управления
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(restartButton);

        // Добавляем панель управления в главное окно
        frame.add(controlPanel);

        // Создание анимации
        animationFactory.createAnimation(frame);

        // Отображение окна
        frame.setVisible(true);
>>>>>>> Stashed changes
    }
}
