package org.foxesworld.animatix.animation.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.textEffect.TextAnimationFrame;

import javax.swing.*;
import java.awt.*;

public class TextZoomInFrame extends TextAnimationFrame {

    public TextZoomInFrame(AnimationFactory animationFactory, String text, String font, int fontSize, String textColor) {
        super(animationFactory, text, font, fontSize, textColor);
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
