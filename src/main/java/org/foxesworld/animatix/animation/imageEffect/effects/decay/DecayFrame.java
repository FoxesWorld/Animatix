package org.foxesworld.animatix.animation.imageEffect.effects.decay;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

public class DecayFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startAlpha", "startAlpha", Double.class, 1.0),
            createParam("endAlpha", "endAlpha", Double.class, 0.0),
            createParam("decaySpeed", "decaySpeed", Integer.class, 5)
    };

    private final String effectName = "decay";

    private Double startAlpha, endAlpha;
    private int decaySpeed;

    public DecayFrame(AnimationFactory animationFactory) {
        super(animationFactory);
        initializeParams(params, effectName);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }
    @Override
    public void update(float progress) {
        float alpha = (float) (startAlpha + progress * (endAlpha - startAlpha));
        BufferedImage decayedImage = imageWorks.applyPixelDecayEffect(imageWorks.getImage(), alpha, decaySpeed);
        label.setIcon(new ImageIcon(decayedImage));
        imageWorks.setImage((BufferedImage) decayedImage);
    }
}
