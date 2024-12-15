package org.foxesworld.animatix.animation.imageEffect.effects.rotate;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RotateFrame extends AnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startAngle", "startAngle", Float.class, 0.0f),
            createParam("endAngle", "endAngle", Float.class, 360.0f)
    };

    private final String effectName = "rotate";

    private float startAngle;
    private float endAngle;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RotateFrame(AnimationFactory animationFactory) {
        super(animationFactory);
        this.label = animationFactory.getImageWorks().getLabel();
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        float currentAngle = startAngle + progress * (endAngle - startAngle);

        executorService.submit(() -> {
            try {
                // Освобождение ресурсов после поворота
                BufferedImage rotatedImage = imageWorks.applyRotationEffect(currentAngle, getAnimationFactory()::dispose);

                if (rotatedImage != null) {
                    SwingUtilities.invokeLater(() -> {
                        label.setIcon(new ImageIcon(rotatedImage));
                    });
                    imageWorks.setImage(rotatedImage);
                }
            } catch (Exception e) {
                AnimationFactory.logger.error("Error during rotation: {}", e.getMessage());
            }
        });
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);

        if (Float.isNaN(startAngle) || Float.isNaN(endAngle)) {
            AnimationFactory.logger.error("Invalid parameters for RotateFrame: startAngle={}, endAngle={}", startAngle, endAngle);
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
