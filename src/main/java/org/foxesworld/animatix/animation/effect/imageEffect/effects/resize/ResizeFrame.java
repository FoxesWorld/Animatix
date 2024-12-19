package org.foxesworld.animatix.animation.effect.imageEffect.effects.resize;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.config.KeyFrame;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageAnimationFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class ResizeFrame extends ImageAnimationFrame {

    private final Map<String, Object>[] params = new Map[]{
            createParam("startWidth", "startWidth", Integer.class, 100),
            createParam("endWidth", "endWidth", Integer.class, 20),
            createParam("startHeight", "startHeight", Integer.class, 100),
            createParam("endHeight", "endHeight", Integer.class, 20),
            createParam("resizeType", "resizeType", String.class, "SCALE_TO_COVER")
    };
    private final String effectName = "resize";

    private int startWidth, endWidth, startHeight, endHeight;
    private String resizeType;

    public ResizeFrame(AnimationFactory animationFactory, KeyFrame keyFrame,  AnimationPhase phase, JLabel label) {
        super(animationFactory, keyFrame, phase, label);
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        int newWidth = (int) (startWidth + progress * (endWidth - startWidth));
        int newHeight = (int) (startHeight + progress * (endHeight - startHeight));


        BufferedImage resizedImage;


            resizedImage = getAnimationFactory().getImageWorks().resizeImage(
                    (BufferedImage) ((ImageIcon) label.getIcon()).getImage(),
                    newWidth,
                    newHeight,
                    ResizeType.valueOf(resizeType)
            );

        label.setIcon(new ImageIcon(resizedImage));
        imageCache.cacheImage(label.getName(), resizedImage);
        label.repaint();
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);
        if (resizeType == null) {
            AnimationFactory.logger.log(System.Logger.Level.WARNING, "resizeType is not set. Using default value: SCALE_TO_COVER");
            resizeType = "SCALE_TO_COVER";
        }
    }

    public enum ResizeType {
        SCALE_TO_COVER,
        SCALE_TO_FIT,
        STRETCH
    }
}
