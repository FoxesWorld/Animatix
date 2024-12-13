package org.foxesworld.animatix.animation.imageEffect.effects.rotate;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class RotateFrame extends AnimationFrame {

    public RotateFrame(AnimationFactory animationFactory) {
        super(animationFactory);
    }

    @Override
    public void update(float progress) {
        float currentAngle = phase.getStartAngle() + progress * (phase.getEndAngle() - phase.getStartAngle());
        BufferedImage rotatedImage = imageWorks.applyRotationEffect(currentAngle);
        label.setIcon(new ImageIcon(rotatedImage));
        imageWorks.setImage(rotatedImage);
    }
}
