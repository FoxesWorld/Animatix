package org.foxesworld.animatix.animation.imageEffect.effects.colorFade;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorFadeFrame extends AnimationFrame {

    private static final Logger logger = LoggerFactory.getLogger(ColorFadeFrame.class);

    private Color startColor;  // Начальный цвет
    private Color endColor;    // Конечный цвет
    private Color currentColor; // Текущий цвет
    private float progress;

    public ColorFadeFrame(AnimationFactory animationFactory, Color startColor, Color endColor) {
        super(animationFactory);
        this.startColor = startColor;
        this.endColor = endColor;
        this.currentColor = startColor;
    }

    @Override
    public void update(float progress) {
        this.progress = progress;

        // Вычисляем текущий цвет (RGB) основываясь на прогрессе
        int r = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * progress);
        int g = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * progress);
        int b = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * progress);

        // Оставляем альфа-канал неизменным
        int alpha = startColor.getAlpha();

        // Новый цвет без изменения альфа-канала
        currentColor = new Color(r, g, b, alpha);

        // Обновляем цвет объекта (текста)
        SwingUtilities.invokeLater(() -> {
            label.setForeground(currentColor);
        });

        // Обновляем изображение, если оно присутствует (для изображений)
        if (imageWorks.getImage() != null) {
            BufferedImage currentImage = imageWorks.getImage();
            for (int y = 0; y < currentImage.getHeight(); y++) {
                for (int x = 0; x < currentImage.getWidth(); x++) {
                    Color pixelColor = new Color(currentImage.getRGB(x, y), true);  // Учитываем альфа-канал

                    // Вычисляем новый цвет пикселя в зависимости от прогресса
                    int newR = (int) (pixelColor.getRed() + (currentColor.getRed() - pixelColor.getRed()) * progress);
                    int newG = (int) (pixelColor.getGreen() + (currentColor.getGreen() - pixelColor.getGreen()) * progress);
                    int newB = (int) (pixelColor.getBlue() + (currentColor.getBlue() - pixelColor.getBlue()) * progress);

                    // Применяем новый цвет пикселя, альфа-канал не изменяется
                    currentImage.setRGB(x, y, new Color(newR, newG, newB, pixelColor.getAlpha()).getRGB());
                }
            }

            // Обновляем изображение в анимации
            SwingUtilities.invokeLater(() -> {
                label.setIcon(new ImageIcon(currentImage));
            });
        }
    }

    @Override
    public void run() {
        super.run();
    }
}
