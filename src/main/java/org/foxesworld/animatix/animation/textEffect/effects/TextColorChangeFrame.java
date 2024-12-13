package org.foxesworld.animatix.animation.textEffect.effects;


import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.textEffect.TextAnimationFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Пример реализации анимации текста с эффектом изменения цвета.
 */
public class TextColorChangeFrame extends TextAnimationFrame {

    private final Color targetColor;

    public TextColorChangeFrame(AnimationFactory animationFactory, String text, String font, int fontSize, String textColor, String targetColor) {
        super(animationFactory, text, font, fontSize, textColor);
        this.targetColor = Color.decode(targetColor);
    }

    @Override
    public void applyEffect(float progress) {
        // Применяем изменение цвета по прогрессу
        int r = (int) (progress * targetColor.getRed() + (1 - progress) * Color.decode(textColor).getRed());
        int g = (int) (progress * targetColor.getGreen() + (1 - progress) * Color.decode(textColor).getGreen());
        int b = (int) (progress * targetColor.getBlue() + (1 - progress) * Color.decode(textColor).getBlue());
        label.setForeground(new Color(r, g, b));

        // Применяем текст и шрифт
        label.setText(text);
        label.setFont(new Font(font, Font.PLAIN, fontSize));

        // Центрируем текст
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
    }
}