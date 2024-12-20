package org.foxesworld.animatix.animation.effect.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.attributes.Phase;
import org.foxesworld.animatix.animation.effect.textEffect.TextSplitter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FadeInTextAnimationFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[] {
            createParam("fadeSpeed", "fadeSpeed", Integer.class, 1000), // Скорость появления
            createParam("spacing", "spacing", Integer.class, 2) // Расстояние между буквами
    };

    private final String effectName = "letterfade";

    private int fadeSpeed, spacing;
    private final Map<JLabel, Point> originalPositions = new HashMap<>();
    private final List<JLabel> letterLabels;

    public FadeInTextAnimationFrame(AnimationFactory animationFactory, Phase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);

        String text = label.getText();
        Font font = label.getFont();
        Color color = label.getForeground();

        this.letterLabels = TextSplitter.splitText(
                text,
                font,
                font.getSize(),
                color,
                (charText, charFont) -> {
                    JLabel charLabel = new JLabel(charText);
                    charLabel.setFont(charFont);
                    charLabel.setForeground(color);
                    charLabel.setOpaque(false);
                    charLabel.setVisible(false);
                    return charLabel;
                }
        );

        int startX = label.getX();
        int startY = label.getY();
        TextSplitter.setInitialPositions(letterLabels, startX, startY, spacing);

        JPanel parentPanel = (JPanel) label.getParent();
        if (parentPanel != null) {
            for (JLabel letterLabel : letterLabels) {
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

        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < letterLabels.size(); i++) {
                JLabel letterLabel = letterLabels.get(i);
                Point originalPosition = originalPositions.get(letterLabel);
                if (originalPosition == null) continue;

                double delay = (double) i / letterLabels.size();
                double adjustedProgress = progress - delay;

                if (adjustedProgress < 0) adjustedProgress = 0;
                if (adjustedProgress > 1) adjustedProgress = 1;

                float alpha = (float) adjustedProgress;

                letterLabel.setForeground(new Color(
                        letterLabel.getForeground().getRed(),
                        letterLabel.getForeground().getGreen(),
                        letterLabel.getForeground().getBlue(),
                        (int)(alpha * 255)
                ));

                if (adjustedProgress > 0) {
                    letterLabel.setVisible(true);
                }

                letterLabel.setLocation(originalPosition.x, originalPosition.y);
            }

            JPanel parentPanel = (JPanel) letterLabels.get(0).getParent();
            if (parentPanel != null) {
                parentPanel.repaint();
            }
        });
    }


}
