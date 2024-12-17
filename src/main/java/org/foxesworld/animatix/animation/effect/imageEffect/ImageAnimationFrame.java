package org.foxesworld.animatix.animation.effect.imageEffect;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import javax.swing.*;

public abstract class ImageAnimationFrame extends AnimationFrame {

    public ImageAnimationFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        this.imageWorks = new ImageWorks(label);
    }

}
