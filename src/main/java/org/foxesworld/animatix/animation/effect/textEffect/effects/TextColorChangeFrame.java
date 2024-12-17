package org.foxesworld.animatix.animation.effect.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class TextColorChangeFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startColor", "startColor", Color.class, Color.BLACK),
            createParam("endColor", "endColor", Color.class, Color.RED)
    };

    private final String effectName = "colorchange";

    private Color startColor, endColor;

    public TextColorChangeFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        if (progress < 0.0f || progress > 1.0f) return;

        // Линейная интерполяция цвета
        int r = (int) ((1 - progress) * startColor.getRed() + progress * endColor.getRed());
        int g = (int) ((1 - progress) * startColor.getGreen() + progress * endColor.getGreen());
        int b = (int) ((1 - progress) * startColor.getBlue() + progress * endColor.getBlue());
        Color currentColor = new Color(r, g, b);

        // Обновляем цвет текста
        SwingUtilities.invokeLater(() -> label.setForeground(currentColor));
    }
}
