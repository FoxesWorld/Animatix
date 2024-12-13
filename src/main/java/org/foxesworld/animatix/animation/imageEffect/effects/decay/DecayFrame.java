package org.foxesworld.animatix.animation.imageEffect.effects.decay;

import javax.swing.*;
import java.awt.image.BufferedImage;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

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
