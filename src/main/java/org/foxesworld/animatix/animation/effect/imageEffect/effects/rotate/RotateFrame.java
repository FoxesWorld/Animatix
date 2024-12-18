package org.foxesworld.animatix.animation.effect.imageEffect.effects.rotate;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageAnimationFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RotateFrame extends ImageAnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startAngle", "startAngle", Float.class, 0.0f),
            createParam("endAngle", "endAngle", Float.class, 360.0f)
    };

    private final String effectName = "rotate";

    private float startAngle;
    private float endAngle;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RotateFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        super(animationFactory, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        float currentAngle = startAngle + progress * (endAngle - startAngle);

        executorService.submit(() -> {
            try {
                BufferedImage rotatedImage = imageWorks.applyRotationEffect(image, currentAngle, getAnimationFactory()::dispose);

                if (rotatedImage != null) {
                    SwingUtilities.invokeLater(() -> {
                        label.setIcon(new ImageIcon(rotatedImage));
                    });
                    imageCache.cacheImage(label.getName(), rotatedImage);
                }
            } catch (Exception e) {
                AnimationFactory.logger.log(System.Logger.Level.ERROR, "Error during rotation: {}", e.getMessage());
            }
        });
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);

        if (Float.isNaN(startAngle) || Float.isNaN(endAngle)) {
            AnimationFactory.logger.log(System.Logger.Level.ERROR, "Invalid parameters for RotateFrame: startAngle={}, endAngle={}", startAngle, endAngle);
            throw new IllegalArgumentException("Parameters 'startAngle' and 'endAngle' must be valid float values.");
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        executorService.shutdown();
        this.getAnimationFactory().dispose();
    }
}
