package org.example.animation.imageEffect.effects;

import javax.swing.*;
import java.awt.image.BufferedImage;

import org.example.animation.AnimationFactory;
import org.example.animation.imageEffect.AnimationFrame;

public class DecayFrame extends AnimationFrame {

    public DecayFrame(AnimationFactory animationFactory) {
        super(animationFactory);
    }

    @Override
    public void update(float progress) {
        float alpha = phase.getStartAlpha() + progress * (phase.getEndAlpha() - phase.getStartAlpha());
        BufferedImage decayedImage = imageWorks.applyPixelDecayEffect(imageWorks.getImage(), alpha, phase.getPixelDecaySpeed());
        label.setIcon(new ImageIcon(decayedImage));
        imageWorks.setImage(decayedImage);
    }
}
