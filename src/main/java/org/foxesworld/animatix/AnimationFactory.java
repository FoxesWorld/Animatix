package org.foxesworld.animatix;

import org.foxesworld.animatix.animation.TaskExecutor;
import org.foxesworld.animatix.animation.effect.AnimationEffectFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.phase.AnimationPhaseExecutor;
import org.foxesworld.animatix.animation.config.AnimationConfigLoader;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.AnimationStatus;
import org.foxesworld.animatix.animation.config.AnimationConfig;
import org.foxesworld.animatix.animation.imageEffect.ImageWorks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AnimationFactory implements AnimationStatus {

    public static final Logger logger = LogManager.getLogger(AnimationFactory.class);
    private final TaskExecutor taskExecutor;
    private final AnimationConfigLoader configLoader = new AnimationConfigLoader();
    private final AnimationEffectFactory effectFactory = new AnimationEffectFactory();
    private final AnimationPhaseExecutor phaseExecutor = new AnimationPhaseExecutor();
    private List<JLabel> animLabels = new ArrayList<>();
    private final Map<AnimationPhase, List<AnimationFrame>> cachedFrames = new HashMap<>();
    private AnimationConfig config;
    private ImageWorks imageWorks;
    private int phaseNum = 0;
    private int labelIndex = 0;
    private boolean isPaused = false;
    private AnimationPhase currentPhase;

    public AnimationFactory(String configPath) {
        this.taskExecutor = new TaskExecutor(this);
        effectFactory.setAnimationFactory(this);
        phaseExecutor.setAnimationFactory(this);
        loadConfig(configPath);
    }

    private void loadConfig(String configPath) {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found!");
            }
            logger.info("Loading animation config...");
            this.config = configLoader.loadConfig(inputStream);
        } catch (Exception e) {
            AnimationFactory.logger.error("Failed to initialize animation factory", e);
        }
    }

    public void createAnimation(Object window) {
        validateConfig();
        for (AnimationConfig.ImageConfig imageConfig : config.getImages()) {
            Rectangle imageBounds = imageConfig.getBounds();
            BufferedImage labelImage = createSizedImage(ImageWorks.getImageFromStream(imageConfig.getImagePath()), imageBounds);

            JLabel animLabel = new JLabel(new ImageIcon(labelImage));
            animLabel.setBounds(imageConfig.getBounds());
            addLabelToWindow(window, animLabel);
            animLabel.setVisible(false);
            this.animLabels.add(animLabel);
            this.imageWorks = new ImageWorks(animLabel, labelIndex);
            for (AnimationPhase phase : imageConfig.getPhases()) {
                new Thread(() -> {
                    try {
                        if (phase.getDelay() > 0) {
                            Thread.sleep(phase.getDelay());
                        }
                        executeAnimation(phase, imageConfig);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        AnimationFactory.logger.error("Animation interrupted", e);
                    }
                    incrementPhase();
                }).start();
                incrementPhase();
            }
            labelIndex += 1;
        }
    }

    private BufferedImage createSizedImage(BufferedImage image, Rectangle size) {
        // Создаём новое изображение нужного размера
        BufferedImage resizedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        // Устанавливаем параметры высокого качества для интерполяции
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем исходное изображение в новом размере
        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose(); // Освобождаем ресурсы Graphics2D

        // Возвращаем новое изображение
        return resizedImage;
    }


    private void addLabelToWindow(Object window, JLabel label) {
        if (window instanceof JFrame) {
            ((JFrame) window).add(label);
        } else if (window instanceof JWindow) {
            ((JWindow) window).add(label);
        } else {
            throw new IllegalArgumentException("Unsupported window type: " + window.getClass().getName());
        }
    }

    @Deprecated
    private int getWindowWidth(Object window) {
        if (window instanceof JFrame) {
            return ((JFrame) window).getWidth();
        } else if (window instanceof JWindow) {
            return ((JWindow) window).getWidth();
        } else {
            throw new IllegalArgumentException("Unsupported window type: " + window.getClass().getName());
        }
    }

    private void executeAnimation(AnimationPhase phase, AnimationConfig.ImageConfig imageConfig) {
        try {
            do {
                currentPhase = phase;
                this.getImageWorks().getLabel().setVisible(true);
                logger.info("Starting phase number {} of {}", phaseNum, imageConfig.getAnimationName());
                List<AnimationFrame> animationFrames = getOrCacheAnimationFrames(phase);
                phaseExecutor.executePhase(this, animationFrames, phaseNum);
                waitForPhaseCompletion(phase.getDuration());
            } while (imageConfig.isRepeat() && phaseNum < imageConfig.getPhases().size());
            logger.info("Animation complete.");
        } catch (Exception e) {
            logger.error("Error during animation execution", e);
        } finally {
            shutdownScheduler();
        }
    }

    private List<AnimationFrame> getOrCacheAnimationFrames(AnimationPhase phase) {
        if (!cachedFrames.containsKey(phase)) {
            cachedFrames.put(phase, effectFactory.createEffectsForPhase(phase));
        }
        return cachedFrames.get(phase);
    }

    private void waitForPhaseCompletion(long duration) {
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            future.join();
        } catch (Exception e) {
            logger.error("Error during waiting for phase completion", e);
        }
    }

    public synchronized void pause() {
        isPaused = true;
        logger.info("Animation paused.");
    }

    public synchronized void resume() {
        isPaused = false;
        logger.info("Animation resumed.");
        notify();
    }

    public synchronized void incrementPhase() {
        phaseNum++;
        logger.debug("Phase incremented to: {}", phaseNum);
    }

    private void shutdownScheduler() {
        taskExecutor.shutdown();
    }

    @Override
    public synchronized void onPhaseCompleted() {
        logger.info("Phase completed, notifying main thread.");
        notify();
    }

    private void validateConfig() {
        if (config == null) {
            throw new IllegalStateException("AnimationConfig must be loaded before creating animation");
        }
    }

    public void dispose() {
        this.labelIndex = 0;
        this.animLabels = new ArrayList<>();
        if (imageWorks != null) {
            imageWorks.dispose();
            imageWorks = null;
        }
        taskExecutor.shutdown();
    }

    public List<JLabel> getAnimLabels() {
        return animLabels;
    }

    public int getLabelIndex() {
        return labelIndex;
    }

    public ImageWorks getImageWorks() {
        return imageWorks;
    }

    public AnimationPhase getCurrentPhase() {
        return currentPhase;
    }

    public int getPhaseNum() {
        return phaseNum;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }
}
