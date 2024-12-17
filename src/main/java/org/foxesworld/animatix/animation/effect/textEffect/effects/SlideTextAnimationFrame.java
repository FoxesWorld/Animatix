package org.foxesworld.animatix.animation.effect.textEffect.effects;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import javax.swing.*;
import java.util.Map;

public class SlideTextAnimationFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startX", "startX", Integer.class, 0),
            createParam("endX", "endX", Integer.class, 100),
            createParam("startY", "startY", Integer.class, 0),
            createParam("endY", "endY", Integer.class, 0)
    };

    private final String effectName = "slideText";

    private int startX, endX, startY, endY;

    public SlideTextAnimationFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        int currentX = startX + (int) ((endX - startX) * progress);
        int currentY = startY + (int) ((endY - startY) * progress);
        SwingUtilities.invokeLater(() -> label.setLocation(currentX, currentY));
    }
}
