package org.foxesworld.animatix.animation.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.textEffect.TextAnimationFrame;

import javax.swing.*;
import java.awt.*;

public class FadeTextAnimationFrame extends TextAnimationFrame {


    public FadeTextAnimationFrame(AnimationFactory animationFactory, AnimationPhase animationPhase, JLabel label) {
        super(animationFactory, animationPhase, label);
        //setDuration(duration);
    }

    @Override
    public void applyEffect(float progress) {
        SwingUtilities.invokeLater(() -> {
            int alpha = (int) (255 * (1 - progress));
            Color originalColor = label.getForeground();
            label.setForeground(new Color(
                    originalColor.getRed(),
                    originalColor.getGreen(),
                    originalColor.getBlue(),
                    alpha
            ));
        });
    }

    @Override
    public void update(float progress) {

    }
}
