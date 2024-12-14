package org.foxesworld.animatix.animation.imageEffect.effects.rotate;

import org.foxesworld.Main;
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

    // Пул потоков для асинхронного рендеринга
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RotateFrame(AnimationFactory animationFactory) {
        super(animationFactory);
        initializeParams(params, effectName);
    }

    @Override
    public void update(float progress) {
        // Вычисление текущего угла поворота
        float currentAngle = startAngle + progress * (endAngle - startAngle);

        // Применение эффекта поворота в отдельном потоке для улучшения производительности
        executorService.submit(() -> {
            try {
                BufferedImage rotatedImage = imageWorks.applyRotationEffect(currentAngle);

                // Если изображение изменилось, обновляем иконку и кешируем
                if (rotatedImage != null) {
                    SwingUtilities.invokeLater(() -> {
                        label.setIcon(new ImageIcon(rotatedImage)); // Обновление UI в главном потоке
                    });
                    imageWorks.setImage(rotatedImage); // Кэширование изображения
                }
            } catch (Exception e) {
                Main.LOGGER.error("Error during rotation: " + e.getMessage());
            }
        });
    }

    @Override
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        super.initializeParams(params, effectName);

        // Валидация параметров углов
        if (Float.isNaN(startAngle) || Float.isNaN(endAngle)) {
            Main.LOGGER.error("Invalid parameters for RotateFrame: startAngle={}, endAngle={}", startAngle, endAngle);
            throw new IllegalArgumentException("Parameters 'startAngle' and 'endAngle' must be valid float values.");
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        executorService.shutdown();  // Закрытие пула потоков, чтобы избежать утечек памяти
    }
}
