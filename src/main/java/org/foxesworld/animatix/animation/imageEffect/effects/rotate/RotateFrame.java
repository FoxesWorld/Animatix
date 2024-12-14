package org.foxesworld.animatix.animation.imageEffect.effects.rotate;

import org.foxesworld.Main;
import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class RotateFrame extends AnimationFrame {

    // Параметры эффекта
    private final Map<String, Object>[] params = new Map[]{
            createParam("startAngle", "startAngle", Float.class, 0.0f),  // Угол начального поворота
            createParam("endAngle", "endAngle", Float.class, 360.0f)    // Угол конечного поворота
    };

    private final String effectName = "rotate";

    // Поля для хранения параметров
    private float startAngle;
    private float endAngle;

    public RotateFrame(AnimationFactory animationFactory) {
        super(animationFactory);
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        // Вычисляем текущий угол поворота
        float currentAngle = startAngle + progress * (endAngle - startAngle);

        // Применяем эффект поворота
        BufferedImage rotatedImage = imageWorks.applyRotationEffect(currentAngle);
        label.setIcon(new ImageIcon(rotatedImage));
        imageWorks.setImage(rotatedImage);
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);

        // Валидация углов поворота
        if (Float.isNaN(startAngle) || Float.isNaN(endAngle)) {
            Main.LOGGER.error("Invalid parameters for RotateFrame: startAngle={}, endAngle={}", startAngle, endAngle);
            throw new IllegalArgumentException("Parameters 'startAngle' and 'endAngle' must be valid float values.");
        }
    }
}
