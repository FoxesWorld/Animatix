package org.foxesworld.animatix.animation.imageEffect;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class ImageAnimationFrame extends AnimationFrame {

    protected ImageWorks imageWorks;

    public ImageAnimationFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        imageWorks = new ImageWorks(label);
    }

    @Override
    public void update(float progress) {

    }

    @Override
    public void dispose() {
        if (imageWorks != null) {
            imageWorks.dispose();
        }
    }
}
