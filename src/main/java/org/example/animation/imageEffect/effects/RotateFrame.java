package org.example.animation.imageEffect.effects;

import org.example.animation.AnimationFactory;
import org.example.animation.imageEffect.AnimationFrame;

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
