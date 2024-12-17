package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.Main;
import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Base class for animation frames with enhanced scalability and reliability.
 */
public abstract class AnimationFrame implements Runnable {

    private static final System.Logger logger = System.getLogger(AnimationFrame.class.getName());

    private final AnimationFactory animationFactory;
    private final long duration;
    protected final ExecutorService executorService;
    private long startTime;
<<<<<<< Updated upstream
    private boolean finished = false;

    protected final AnimationPhase phase;
    protected final ImageWorks imageWorks;
    protected final JLabel label;

    public AnimationFrame(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
        this.phase = animationFactory.getCurrentPhase();
        this.imageWorks = animationFactory.getImageWorks();
        this.label = animationFactory.getAnimLabel();
=======
    private volatile boolean finished = false;
    protected final AnimationPhase phase;
    protected JLabel label;

    private static int DEFAULT_FPS = 60;
    private static final int MAX_THREADS = 4; // Max number of concurrent threads for animations

    public AnimationFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        this.animationFactory = animationFactory;
        this.phase = phase;
        this.label = label;

>>>>>>> Stashed changes
        this.duration = phase.getDuration();
        // Create a thread pool with a fixed number of threads, adjust as needed
        this.executorService = Executors.newFixedThreadPool(MAX_THREADS);
    }

    /**
<<<<<<< Updated upstream
     * Метод для обновления состояния анимации. Реализуется подклассами.
     *
     * @param progress Процент завершения анимации (0.0 - 1.0)
=======
     * Abstract method to update animation state, implemented by subclasses.
     *
     * @param progress Percentage of animation completion (0.0 - 1.0).
>>>>>>> Stashed changes
     */
    public abstract void update(float progress);

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        finished = false;

<<<<<<< Updated upstream
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
=======
        long frameInterval = 1000 / DEFAULT_FPS;
        executorService.submit(() -> schedulerAtFixedRate(frameInterval));

        logger.log(System.Logger.Level.INFO, "Animation started for phase: {0}", phase.getPhaseNum());
>>>>>>> Stashed changes
    }

    private void schedulerAtFixedRate(long frameInterval) {
        try {
            long elapsedTime = 0;
            while (!finished) {
                elapsedTime = System.currentTimeMillis() - startTime;
                float progress = Math.min((float) elapsedTime / duration, 1.0f);

<<<<<<< Updated upstream
        // Обновляем UI
        SwingUtilities.invokeLater(() -> {
            try {
                update(progress);
            } catch (Exception ex) {
                AnimationFactory.logger.error("Error in UI update", ex);
=======
                SwingUtilities.invokeLater(() -> update(progress));

                if (elapsedTime >= duration) {
                    stopAnimation();
                    SwingUtilities.invokeLater(() -> update(1.0f)); // Final update
                    logger.log(System.Logger.Level.INFO, "Animation completed for phase: {0}", phase.getEffects());

                    if (animationFactory instanceof AnimationStatus) {
                        ((AnimationStatus) animationFactory).onPhaseCompleted(phase);
                    }
                    break;
                }

                Thread.sleep(frameInterval);
>>>>>>> Stashed changes
            }
        } catch (Exception ex) {
            logger.log(System.Logger.Level.ERROR, "Error during animation frame update", ex);
            stopAnimation();
<<<<<<< Updated upstream
            SwingUtilities.invokeLater(() -> update(1.0f));
            AnimationFactory.logger.info("Animation completed for phase: {}", animationFactory.getPhaseNum());
            animationFactory.incrementPhase();

            if (animationFactory instanceof AnimationStatus) {
                ((AnimationStatus) animationFactory).onPhaseCompleted();
            }
=======
>>>>>>> Stashed changes
        }
    }

    private void stopAnimation() {
        finished = true;
        logger.log(System.Logger.Level.INFO, "Animation stopped.");
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

<<<<<<< Updated upstream
                // Получаем значение параметра из конфигурации фазы
=======
>>>>>>> Stashed changes
                Object value = phase.getEffectParam(effectName, paramName, type);
                if (value == null) {
                    value = defaultValue;
                }

<<<<<<< Updated upstream
                // Устанавливаем значение в поле
                setFieldValue(field, value);
            } catch (Exception e) {
                AnimationFactory.logger.error("Error initializing parameter: {}", param, e);
=======
                setFieldValue(field, value);
            } catch (Exception e) {
                logger.log(System.Logger.Level.ERROR, "Error initializing parameter: {0}", param, e);
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
     * Метод для очистки ресурсов и завершения анимации.
=======
     * Disposes of resources and stops the animation.
>>>>>>> Stashed changes
     */
    public void dispose() {
        try {
            if (!executorService.isShutdown()) {
                executorService.shutdownNow();
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            logger.log(System.Logger.Level.WARNING, "Interrupted during executor termination", e);
            Thread.currentThread().interrupt();
        }
        finished = true;
    }

    public AnimationFactory getAnimationFactory() {
        return animationFactory;
    }

    public static void setDefaultFps(int defaultFps) {
        DEFAULT_FPS = defaultFps;
    }
}
