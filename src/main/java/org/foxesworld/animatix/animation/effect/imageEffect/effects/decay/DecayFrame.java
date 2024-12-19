package org.foxesworld.animatix.animation.effect.imageEffect.effects.decay;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.config.KeyFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageAnimationFrame;

import javax.swing.*;
import java.util.Map;

public class DecayFrame extends ImageAnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startAlpha", "startAlpha", Double.class, 1.0),
            createParam("endAlpha", "endAlpha", Double.class, 0.0),
            createParam("decaySpeed", "decaySpeed", Integer.class, 5)
    };

    private final String effectName = "decay";

    private Double startAlpha, endAlpha;
    private int decaySpeed;

    public DecayFrame(AnimationFactory animationFactory, KeyFrame keyFrame, AnimationPhase phase, JLabel label) {
        super(animationFactory, keyFrame, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        float alpha = (float) (startAlpha + progress * (endAlpha - startAlpha));

        image = imageWorks.applyPixelDecayEffect(image, alpha, decaySpeed);
        imageCache.cacheImage(label.getName(), image);


        label.setIcon(new ImageIcon(image));
    }
}
