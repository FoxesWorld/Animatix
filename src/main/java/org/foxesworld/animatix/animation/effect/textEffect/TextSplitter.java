package org.foxesworld.animatix.animation.effect.textEffect;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class TextSplitter {

    /**
     * Разбивает текст на символы и создает список JLabel для каждого символа.
     *
     * @param text     Текст для разбивки.
     * @param font     Шрифт, который будет применён к JLabel.
     * @param fontSize Размер шрифта.
     * @param color    Цвет текста.
     * @return Список JLabel для каждого символа.
     */
    public static <T extends JLabel> List<T> splitText(
            String text,
            Font font,
            int fontSize,
            Color color,
            BiFunction<String, Font, T> labelFactory) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        if (font == null) {
            font = new Font("Arial", Font.PLAIN, fontSize); // Шрифт по умолчанию
        } else {
            font = font.deriveFont((float) fontSize);
        }

        List<T> labels = new ArrayList<>();
        for (char c : text.toCharArray()) {
            T label = labelFactory.apply(String.valueOf(c), font);
            label.setForeground(color);
            label.setOpaque(false);
            labels.add(label);
        }
        return labels;
    }
    /**
     * Устанавливает начальное расположение символов для анимации.
     *
     * @param labels  Список JLabel.
     * @param startX  Начальная координата X.
     * @param startY  Начальная координата Y.
     * @param spacing Расстояние между символами.
     */
    public static void setInitialPositions(List<? extends JLabel> labels, int startX, int startY, int spacing) {
        int currentX = startX;
        for (JLabel label : labels) {
            label.setBounds(currentX, startY, label.getPreferredSize().width, label.getPreferredSize().height);
            currentX += spacing + label.getPreferredSize().width;
        }
    }

}
