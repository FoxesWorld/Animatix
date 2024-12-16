package org.foxesworld.animatix.animation.imageEffect.effects.resize;

import org.foxesworld.animatix.Main;
import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class ResizeFrame extends AnimationFrame {

    // Параметры эффекта
    private final Map<String, Object>[] params = new Map[]{
            createParam("startWidth", "startWidth", Integer.class, 100),
            createParam("endWidth", "endWidth", Integer.class, 20),
            createParam("startHeight", "startHeight", Integer.class, 100),
            createParam("endHeight", "endHeight", Integer.class, 20),
            createParam("resizeType", "resizeType", String.class, "SCALE_TO_COVER")
    };
    private final String effectName = "resize";

    // Поля для хранения параметров
    private int startWidth, endWidth, startHeight, endHeight;
    private String resizeType;

    public ResizeFrame(AnimationFactory animationFactory) {
        super(animationFactory);
        this.label = animationFactory.getImageWorks().getLabel();
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        // Вычисляем новые размеры
        int newWidth = (int) (startWidth + progress * (endWidth - startWidth));
        int newHeight = (int) (startHeight + progress * (endHeight - startHeight));

        // Меняем размеры изображения
        BufferedImage resizedImage = imageWorks.resizeImage(newWidth, newHeight, ResizeType.valueOf(resizeType));
        label.setIcon(new ImageIcon(resizedImage));
        //label.setSize(newWidth, newHeight);
        imageWorks.setImage(resizedImage);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);

        // Убедимся, что resizeType правильно задан
        if (resizeType == null) {
            AnimationFactory.logger.warn("resizeType is not set. Using default value: SCALE_TO_COVER");
            resizeType = "SCALE_TO_COVER";
        }
    }

    /**
     * Типы изменения размеров.
     */
    public enum ResizeType {
        SCALE_TO_COVER,
        SCALE_TO_FIT,
        STRETCH
    }
}
