package org.foxesworld.animatix.animation.effect.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.Phase;
import org.foxesworld.animatix.animation.effect.textEffect.TextSplitter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlipTextAnimationFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("flipSpeed", "flipSpeed", Integer.class, 1000), // Скорость разворота
            createParam("spacing", "spacing", Integer.class, 2)
    };

    private final String effectName = "flip";

    private int flipSpeed, spacing;
    private final Map<FlippingLabel, Point> originalPositions = new HashMap<>();
    private final List<FlippingLabel> letterLabels;

    public FlipTextAnimationFrame(AnimationFactory animationFactory, Phase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);

        // Разбиваем текст на отдельные символы с использованием FlippingLabel
        String text = label.getText();
        Font font = label.getFont();
        Color color = label.getForeground();

        this.letterLabels = TextSplitter.splitText(
                text,
                font,
                font.getSize(),
                color,
                (charText, charFont) -> new FlippingLabel(charText, charFont, color)
        );

        int startX = label.getX();
        int startY = label.getY();
        TextSplitter.setInitialPositions(letterLabels, startX, startY, spacing);

        // Добавляем символы в контейнер родителя
        JPanel parentPanel = (JPanel) label.getParent();
        if (parentPanel != null) {
            for (FlippingLabel letterLabel : letterLabels) {
                parentPanel.add(letterLabel);
                originalPositions.put(letterLabel, letterLabel.getLocation());
            }
            parentPanel.remove(label);
            parentPanel.revalidate();
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
        float scaledProgress = progress * (1000.0f / flipSpeed);

        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < letterLabels.size(); i++) {
                FlippingLabel letterLabel = letterLabels.get(i);
                Point originalPosition = originalPositions.get(letterLabel);
                if (originalPosition == null) continue;

                // Рассчитываем прогресс для текущей буквы с учетом задержки
                double delay = (double) i / letterLabels.size();
                double adjustedProgress = scaledProgress - delay;

                if (adjustedProgress < 0) adjustedProgress = 0;
                if (adjustedProgress > 1) adjustedProgress = 1;

                // Расчет угла поворота (0° -> 180°)
                double angle = adjustedProgress * 180;
                letterLabel.setRotationAngle(angle);
            }
        });
    }

    /**
     * Вложенный класс для анимации разворота символов.
     */
    public static class FlippingLabel extends JLabel {
        private double rotationAngle = 0;

        public FlippingLabel(String text, Font font, Color color) {
            super(text);
            setFont(font);
            setForeground(color);
            setOpaque(false); // Убираем фон
        }

        public void setRotationAngle(double angle) {
            this.rotationAngle = angle;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Устанавливаем сглаживание
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Поворачиваем компонент
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            g2d.rotate(Math.toRadians(rotationAngle), centerX, centerY);

            // Рисуем текст
            super.paintComponent(g2d);

            g2d.dispose();
        }
    }
}
