package org.foxesworld.animatix.animation.effect.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.effect.textEffect.TextAnimationFrame;

import javax.swing.*;
import java.awt.*;

public class TextZoomInFrame extends TextAnimationFrame {

    public TextZoomInFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
    }

    @Override
    public void applyEffect(float progress) {
        int currentFontSize = (int) (fontSize * progress);
        label.setText(text);
        label.setFont(new Font(font, Font.PLAIN, currentFontSize));
        label.setForeground(Color.decode(textColor));

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
    }
}
