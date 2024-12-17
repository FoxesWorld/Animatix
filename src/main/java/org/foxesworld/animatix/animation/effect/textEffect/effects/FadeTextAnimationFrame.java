package org.foxesworld.animatix.animation.effect.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class FadeTextAnimationFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("fadeType", "fadeType", String.class, "fadeIn"),
            createParam("startAlpha", "startAlpha", Double.class, 1.0),
            createParam("endAlpha", "endAlpha", Double.class, 0.0),
            createParam("fadeSpeed", "fadeSpeed", Integer.class, 5)
    };

    private final String effectName = "fade";

    private Double startAlpha, endAlpha;
    private String fadeType;
    private int fadeSpeed;

    public FadeTextAnimationFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);

        if ("fadeIn".equalsIgnoreCase(fadeType)) {
            startAlpha = 0.0;
            endAlpha = 1.0;
        } else if ("fadeOut".equalsIgnoreCase(fadeType)) {
            startAlpha = 1.0;
            endAlpha = 0.0;
        }
    }

    @Override
    public void update(float progress) {
        // Рассчитываем текущее значение альфа
        float alpha = (float) (startAlpha + progress * (endAlpha - startAlpha));
        int intAlpha = Math.max(0, Math.min(255, (int) (alpha * 255)));

        // Применяем альфа-канал к тексту
        SwingUtilities.invokeLater(() -> {
            Color originalColor = label.getForeground();
            label.setForeground(new Color(
                    originalColor.getRed(),
                    originalColor.getGreen(),
                    originalColor.getBlue(),
                    intAlpha
            ));
        });
    }
}
