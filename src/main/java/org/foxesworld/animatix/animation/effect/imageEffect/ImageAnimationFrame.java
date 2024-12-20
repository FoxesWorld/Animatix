package org.foxesworld.animatix.animation.effect.imageEffect;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.cache.ImageCache;
import org.foxesworld.animatix.animation.config.attributes.Phase;

import javax.swing.*;
import java.awt.image.BufferedImage;

public abstract class ImageAnimationFrame extends AnimationFrame {
    protected ImageCache imageCache;
    protected BufferedImage image;
    public ImageAnimationFrame(AnimationFactory animationFactory, Phase phase, JLabel label) {
        super(animationFactory, phase, label);
        this.imageCache = animationFactory.getImageCache();
        this.image = this.imageCache.getCachedImage(label.getName());
        this.imageWorks = animationFactory.getImageWorks();
    }

}
