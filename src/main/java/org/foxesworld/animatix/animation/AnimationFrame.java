package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.imageEffect.ImageWorks;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class AnimationFrame implements Runnable {

    private static final int DEFAULT_FPS = 60;
    private final AnimationFactory animationFactory;
    private final long duration;
    private Timer timer;
    private long startTime;
    private boolean finished = false;

    protected final AnimationPhase phase;
    protected final ImageWorks imageWorks;
    protected JLabel label;

    public AnimationFrame(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
        this.phase = animationFactory.getCurrentPhase();
        this.imageWorks = animationFactory.getImageWorks();
        //this.label = animationFactory.getAnimLabels().get(imageWorks.getLabelIndex());
        this.duration = phase.getDuration();
    }

    /**
     * Метод для обновления состояния анимации. Реализуется подклассами.
     *
     * @param progress Процент завершения анимации (0.0 - 1.0)
     */
    public abstract void update(float progress);

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        finished = false;

        timer = new Timer(1000 / DEFAULT_FPS, e -> {
            try {
                updateFrame();
            } catch (Exception ex) {
                AnimationFactory.logger.error("Error during animation frame update", ex);
                stopAnimation();
            }
        });

        timer.start();
        AnimationFactory.logger.info("Animation started for phase: {}", animationFactory.getPhaseNum());
    }

    private void updateFrame() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        float progress = Math.min((float) elapsedTime / duration, 1.0f);

        // Обновляем UI
        SwingUtilities.invokeLater(() -> {
            try {
                update(progress);
            } catch (Exception ex) {
                AnimationFactory.logger.error("Error in UI update", ex);
            }
        });

        if (elapsedTime >= duration) {
            stopAnimation();
            SwingUtilities.invokeLater(() -> update(1.0f));
            AnimationFactory.logger.info("Animation completed for phase: {}", animationFactory.getPhaseNum());
            animationFactory.incrementPhase();

            if (animationFactory instanceof AnimationStatus) {
                ((AnimationStatus) animationFactory).onPhaseCompleted();
            }
        }
    }

    private void stopAnimation() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public AnimationPhase getPhase() {
        return phase;
    }

    /**
     * нициализация параметров с использованием конфигурации фазы.
     *
     * @param params     Карта параметров.
     * @param effectName Название эффекта.
     */
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        for (Map<String, Object> param : params) {
            try {
                String field = (String) param.get("field");
                String paramName = (String) param.get("paramName");
                Class<?> type = (Class<?>) param.get("type");
                Object defaultValue = param.get("defaultValue");

                // Получаем значение параметра из конфигурации фазы
                Object value = phase.getEffectParam(effectName, paramName, type);
                if (value == null) {
                    value = defaultValue;
                }

                // Устанавливаем значение в поле
                setFieldValue(field, value);
            } catch (Exception e) {
                AnimationFactory.logger.error("Error initializing parameter: {}", param, e);
            }
        }
    }

    /**
     * Устанавливает значение поля через рефлексию.
     *
     * @param fieldName Название поля.
     * @param value     Значение для установки.
     */
    private void setFieldValue(String fieldName, Object value) throws Exception {
        Field field = this.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(this, value);
    }

    /**
     * Создает карту параметров для инициализации.
     *
     * @param field        Название поля.
     * @param paramName    Название параметра.
     * @param type         Тип значения.
     * @param defaultValue Значение по умолчанию.
     * @return Карта параметров.
     */
    protected Map<String, Object> createParam(String field, String paramName, Class<?> type, Object defaultValue) {
        Map<String, Object> param = new HashMap<>();
        param.put("field", field);
        param.put("paramName", paramName);
        param.put("type", type);
        param.put("defaultValue", defaultValue);
        return param;
    }

    /**
     * Метод для очистки ресурсов и завершения анимации.
     */
    public void dispose() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        finished = true;
        if (imageWorks != null) {
            imageWorks.dispose();
        }
    }

    public AnimationFactory getAnimationFactory() {
        return animationFactory;
    }
}
