package org.foxesworld.animatix.animation.imageEffect.effects.crackFrame;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrackFrame extends AnimationFrame {

    private List<Crack> cracks;  // Список трещин
    private Random random;

    public CrackFrame(AnimationFactory animationFactory) {
        super(animationFactory);
        cracks = new ArrayList<>();
        random = new Random();
    }

    @Override
    public void update(float progress) {
        // Применяем прогресс к размеру трещин
        for (Crack crack : cracks) {
            crack.update(progress);
        }

        // Рисуем трещины на изображении
        drawCracks(progress);
    }

    @Override
    public void run() {
        super.run();

        // Создаем случайные трещины
        generateCracks();
    }

    private void generateCracks() {
        // Генерация случайных трещин на изображении
        int numCracks = 5 + random.nextInt(5); // 5-10 случайных трещин
        for (int i = 0; i < numCracks; i++) {
            cracks.add(new Crack(random.nextInt(label.getWidth()), random.nextInt(label.getHeight())));
        }
    }

    private void drawCracks(float progress) {
        // Получаем текущее изображение для рисования трещин
        BufferedImage currentImage = imageWorks.getImage();
        if (currentImage == null) return;

        Graphics2D g = currentImage.createGraphics();
        g.setColor(Color.BLACK); // Цвет трещин
        g.setStroke(new BasicStroke(2));  // Устанавливаем толщину линии для трещин

        // Рисуем каждую трещину, увеличивая ее с прогрессом
        for (Crack crack : cracks) {
            crack.draw(g, progress);
        }

        // Завершаем рисование
        g.dispose();

        // Обновляем изображение в анимации
        SwingUtilities.invokeLater(() -> {
            label.setIcon(new ImageIcon(currentImage));
        });
    }

    // Внутренний класс для описания трещин
    private class Crack {
        private int startX, startY, endX, endY;
        private float length;
        private float maxLength = 100f; // Максимальная длина трещины
        private int angle;

        public Crack(int startX, int startY) {
            this.startX = startX;
            this.startY = startY;
            this.angle = random.nextInt(360);
            this.length = 0;
            // Определяем случайную конечную точку для трещины
            this.endX = startX + (int) (Math.cos(Math.toRadians(angle)) * maxLength);
            this.endY = startY + (int) (Math.sin(Math.toRadians(angle)) * maxLength);
        }

        public void update(float progress) {
            // Увеличиваем длину трещины с прогрессом
            length = maxLength * progress;
        }

        public void draw(Graphics2D g, float progress) {
            // Рисуем трещину
            int x1 = startX;
            int y1 = startY;
            int x2 = (int) (startX + Math.cos(Math.toRadians(angle)) * length);
            int y2 = (int) (startY + Math.sin(Math.toRadians(angle)) * length);

            g.drawLine(x1, y1, x2, y2);
        }
    }
}
