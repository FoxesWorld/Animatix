package org.foxesworld.animatix.animation.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.textEffect.TextAnimationFrame;

import javax.swing.*;

public class SlideTextAnimationFrame extends TextAnimationFrame {

    private final int startX;
    private final int endX;

    public SlideTextAnimationFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label); // Текстовые параметры здесь не используются
        this.startX = label.getX();
        this.endX = startX + 100; // Сдвиг на 100 пикселей вправо
        //setDuration(duration);
    }

    @Override
    public void applyEffect(float progress) {
        SwingUtilities.invokeLater(() -> {
            int newX = startX + (int) ((endX - startX) * progress);
            label.setLocation(newX, label.getY());
        });
    }
}
