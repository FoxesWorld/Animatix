package org.foxesworld.animatix.animation.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.textEffect.TextSplitter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BounceTextAnimationFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("bounceHeight", "bounceHeight", Integer.class, 20),
            createParam("bounceSpeed", "bounceSpeed", Integer.class, 1000), // Скорость анимации
            createParam("spacing", "spacing", Integer.class, 2)
    };

    private final String effectName = "bounce";

    private int bounceHeight, bounceSpeed, spacing;
    private final Map<JLabel, Point> originalPositions = new HashMap<>();
    private final List<JLabel> letterLabels;

    // Храним исходную позицию самого label
    private final Point labelOriginalPosition;

    public BounceTextAnimationFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);

        // Сохраняем исходную позицию самого label
        labelOriginalPosition = new Point(label.getX(), label.getY());

        // Разбиваем текст на отдельные символы
        String text = label.getText();
        Font font = label.getFont();
        Color color = label.getForeground();
        this.letterLabels = TextSplitter.splitText(text, font, font.getSize(), color);

        int startX = label.getX();
        int startY = label.getY();
        TextSplitter.setInitialPositions(letterLabels, startX + 23, startY + 6, spacing);

        // Добавляем символы в контейнер родителя
        JPanel parentPanel = (JPanel) label.getParent();

        if (parentPanel != null) {
            for (JLabel letterLabel : letterLabels) {
                parentPanel.add(letterLabel);
                originalPositions.put(letterLabel, letterLabel.getLocation());
            }
            parentPanel.remove(label);  // Убираем исходную метку
            parentPanel.revalidate();    // Перерисовываем родительский контейнер
            parentPanel.repaint();
        }
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        if (letterLabels.isEmpty()) return;
        float scaledProgress = progress * (1000.0f / bounceSpeed);

        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < letterLabels.size(); i++) {
                JLabel letterLabel = letterLabels.get(i);
                Point originalPosition = originalPositions.get(letterLabel);
                if (originalPosition == null) continue;

                double delay = (double) i / letterLabels.size();
                double adjustedProgress = Math.min(Math.max(scaledProgress - delay, 0), 1);

                int bounceOffset = (int) (Math.sin(adjustedProgress * Math.PI) * bounceHeight);
                letterLabel.setLocation(
                        originalPosition.x,
                        labelOriginalPosition.y - bounceOffset
                );
            }

            for (JLabel letterLabel : letterLabels) {
                letterLabel.repaint();
            }
        });
    }

}
