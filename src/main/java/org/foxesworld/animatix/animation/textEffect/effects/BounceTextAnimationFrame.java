package org.foxesworld.animatix.animation.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BounceTextAnimationFrame extends AnimationFrame {

    private final Map<Integer, Point> originalPositions = new HashMap<>();
    private final int bounceHeight;
    private final int duration;

    public BounceTextAnimationFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);

        // Задаем параметры по умолчанию
        this.bounceHeight = 20; // Высота прыжка в пикселях
        this.duration = 1000; // Продолжительность анимации прыжка для каждой буквы

        // Подготовка оригинальных позиций для каждого символа
        initializeOriginalPositions();
    }

    private void initializeOriginalPositions() {
        String text = label.getText();
        if (text == null || text.isEmpty()) return;

        // Получаем ширину и высоту каждого символа
        FontMetrics metrics = label.getFontMetrics(label.getFont());
        int xOffset = 0;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            int charWidth = metrics.charWidth(ch);

            // Сохраняем начальную позицию каждого символа
            originalPositions.put(i, new Point(xOffset, 0));
            xOffset += charWidth;
        }
    }

    @Override
    public void update(float progress) {
        String text = label.getText();
        if (text == null || text.isEmpty()) return;

        // Переинициализация оригинальных позиций, если текст изменился
        if (originalPositions.isEmpty() || originalPositions.size() != text.length()) {
            initializeOriginalPositions();
        }

        SwingUtilities.invokeLater(() -> {
            StringBuilder styledText = new StringBuilder("<html><div style='position:relative; display:inline-block;'>");
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                Point originalPosition = originalPositions.get(i);
                if (originalPosition == null) continue; // Защита от NPE

                // Вычисление смещения по Y
                int bounceOffset = (int) (Math.sin(progress * Math.PI * 2) * bounceHeight);
                int charX = originalPosition.x;
                int charY = originalPosition.y - bounceOffset;

                styledText.append("<span style='position:absolute; left:")
                        .append(charX)
                        .append("px; top:")
                        .append(charY)
                        .append("px;'>")
                        .append(ch)
                        .append("</span>");
            }
            styledText.append("</div></html>");
            label.setText(styledText.toString());
        });
    }


}
