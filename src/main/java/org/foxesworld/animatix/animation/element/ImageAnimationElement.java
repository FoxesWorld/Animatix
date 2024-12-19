package org.foxesworld.animatix.animation.element;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageAnimationElement extends BaseAnimationElement {
    private BufferedImage image;

    public ImageAnimationElement(String name, Rectangle bounds, boolean visible, BufferedImage image) {
        super(name, bounds, visible);
        this.image = image;
    }

    @Override
    public JComponent createComponent() {
        JLabel label = new JLabel(new ImageIcon(image));
        label.setBounds(bounds);
        label.setVisible(visible);
        if (Boolean.getBoolean("System.tracers")) {
            image = createReflectedImage(image);
        }
        return label;
    }
    private BufferedImage createReflectedImage(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        // Создаем новое изображение для отражения (с границей)
        BufferedImage reflected = new BufferedImage(width, height * 2, original.getType());
        Graphics2D g2d = reflected.createGraphics();

        // Рисуем оригинальное изображение
        g2d.drawImage(original, 0, 0, null);

        // Рисуем границу для оригинального изображения
        g2d.setColor(Color.RED); // Цвет рамки
        g2d.setStroke(new BasicStroke(2)); // Толщина рамки
        g2d.drawRect(0, 0, width - 1, height - 1); // Граница вокруг оригинала

        // Создаем отражение
        g2d.scale(1, -1); // Ожидаем зеркальное отражение по вертикали
        g2d.drawImage(original, 0, -height * 2, null);

        // Рисуем границу для отражения
        g2d.setColor(Color.BLUE); // Цвет рамки для отражения
        g2d.setStroke(new BasicStroke(2)); // Толщина рамки
        g2d.drawRect(0, -height * 2, width - 1, height - 1); // Граница для отражения

        g2d.dispose();
        return reflected;
    }

}
