package org.foxesworld.animatix.animation.imageEffect.effects.resize;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class ResizeFrame extends AnimationFrame {

    public ResizeFrame(AnimationFactory animationFactory) {
        super(animationFactory);
    }

    @Override
    public void update(float progress) {
        int newWidth = (int) (phase.getStartWidth() + progress * (phase.getEndWidth() - phase.getStartWidth()));
        int newHeight = (int) (phase.getStartHeight() + progress * (phase.getEndHeight() - phase.getStartHeight()));

        BufferedImage resizedImage = imageWorks.resizeImage(newWidth, newHeight, phase.getResizeType());
        label.setIcon(new ImageIcon(resizedImage));
        label.setSize(newWidth, newHeight);
        imageWorks.setImage(resizedImage);
    }
}
