package org.foxesworld.animatix.animation.imageEffect.effects.fade;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;

public class FadeFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("fadeType", "fadeType", String.class, "fadeIn"),
            createParam("startAlpha", "startAlpha", Double.class, 1.0),
            createParam("endAlpha", "endAlpha", Double.class, 0.0),
            createParam("fadeSpeed", "fadeSpeed", Integer.class, 5)
    };

    private final String effectName = "fade";

    private Double startAlpha, endAlpha;
    private String fadeType;
    private int fadeSpeed;

    public FadeFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);

        if ("fadeIn".equalsIgnoreCase(fadeType)) {
            startAlpha = 0.0;
            endAlpha = 1.0;
        } else if ("fadeOut".equalsIgnoreCase(fadeType)) {
            startAlpha = 1.0;
            endAlpha = 0.0;
        }
    }

    @Override
    public void update(float progress) {
        float alpha = (float) (startAlpha + progress * (endAlpha - startAlpha));
        BufferedImage fadedImage = imageWorks.applyAlphaEffect(imageWorks.getImage(), alpha, fadeSpeed);
        label.setIcon(new ImageIcon(fadedImage));
        imageWorks.setImage(fadedImage);
    }
}
