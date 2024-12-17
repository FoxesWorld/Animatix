package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageWorks;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class AnimationFrame implements Runnable {

    private static int DEFAULT_FPS = 144;
    private final AnimationFactory animationFactory;
    private final long duration;
    private Timer timer;
    private long startTime;
    private boolean finished = false;
    protected final AnimationPhase phase;
    protected ImageWorks imageWorks;
    protected JLabel label;

    public AnimationFrame(AnimationFactory animationFactory, AnimationPhase phase, JLabel label) {
        this.animationFactory = animationFactory;
        this.phase = phase;
        this.label = label;

        this.duration = phase.getDuration();
    }

    /**
     * Method to update the animation state. Implemented by subclasses.
     *
     * @param progress Percentage of animation completion (0.0 - 1.0)
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
                AnimationFactory.logger.log(System.Logger.Level.ERROR, "Error during animation frame update", ex);
                stopAnimation();
            }
        });

        timer.start();
        //AnimationFactory.logger.log(System.Logger.Level.INFO, "Animation started for phase: " + animationFactory.getPhaseNum());
    }

    private void updateFrame() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        float progress = Math.min((float) elapsedTime / duration, 1.0f);

        // Update UI
        SwingUtilities.invokeLater(() -> {
            try {
                update(progress);
            } catch (Exception ex) {
                AnimationFactory.logger.log(System.Logger.Level.ERROR, "Error in UI update", ex);
            }
        });

        if (elapsedTime >= duration) {
            stopAnimation();
            SwingUtilities.invokeLater(() -> update(progress));
            //AnimationFactory.logger.log(System.Logger.Level.INFO, "Animation completed for phase: " + animationFactory.getPhaseNum());

            if (animationFactory instanceof AnimationStatus) {
                ((AnimationStatus) animationFactory).onPhaseCompleted(phase);
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
     * Initializes parameters using phase configuration.
     *
     * @param params     Map of parameters.
     * @param effectName Effect name.
     */
    protected void initializeParams(Map<String, Object>[] params, String effectName) {
        for (Map<String, Object> param : params) {
            try {
                String field = (String) param.get("field");
                String paramName = (String) param.get("paramName");
                Class<?> type = (Class<?>) param.get("type");
                Object defaultValue = param.get("defaultValue");

                // Get parameter value from phase configuration
                Object value = phase.getEffectParam(effectName, paramName, type);
                if (value == null) {
                    value = defaultValue;
                }

                // Set value to field
                setFieldValue(field, value);
            } catch (Exception e) {
                AnimationFactory.logger.log(System.Logger.Level.ERROR, "Error initializing parameter: {}", param, e);
            }
        }
    }

    /**
     * Sets the value of a field using reflection.
     *
     * @param fieldName Field name.
     * @param value     Value to set.
     */
    private void setFieldValue(String fieldName, Object value) throws Exception {
        Field field = this.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(this, value);
    }

    /**
     * Creates a parameter map for initialization.
     *
     * @param field        Field name.
     * @param paramName    Parameter name.
     * @param type         Value type.
     * @param defaultValue Default value.
     * @return Parameter map.
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
     * Method to clean up resources and end the animation.
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

    public static void setDefaultFps(int defaultFps) {
        DEFAULT_FPS = defaultFps;
    }
}
