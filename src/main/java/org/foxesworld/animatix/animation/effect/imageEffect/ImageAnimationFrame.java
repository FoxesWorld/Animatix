package org.foxesworld.animatix.animation.effect.imageEffect;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.cache.ImageCache;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.config.KeyFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;

public abstract class ImageAnimationFrame extends AnimationFrame {
    protected ImageCache imageCache;
    protected BufferedImage image;
    public ImageAnimationFrame(AnimationFactory animationFactory, KeyFrame keyFrame, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, keyFrame, label);
        this.imageCache = animationFactory.getImageCache();
        this.image = this.imageCache.getCachedImage(label.getName());
        this.imageWorks = animationFactory.getImageWorks();
    }

}
