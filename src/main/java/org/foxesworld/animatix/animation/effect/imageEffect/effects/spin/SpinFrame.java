package org.foxesworld.animatix.animation.effect.imageEffect.effects.spin;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.attributes.Phase;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageAnimationFrame;

public class SpinFrame extends ImageAnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startAngle", "startAngle", Double.class, 0.0),
            createParam("endAngle", "endAngle", Double.class, 360.0),
            createParam("spinSpeed", "spinSpeed", Integer.class, 5)
    };

    private final String effectName = "spin";

    private double startAngle, endAngle;
    private int spinSpeed;

    public SpinFrame(AnimationFactory animationFactory, Phase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        double currentAngle = startAngle + progress * (endAngle - startAngle);
        BufferedImage rotatedImage = getAnimationFactory().getImageWorks().applyRotationEffect(imageCache.getCachedImage(label.getName()), startAngle, endAngle, spinSpeed, null);
        label.setIcon(new ImageIcon(rotatedImage));
        imageCache.cacheImage(label.getName(), rotatedImage);
    }
}
