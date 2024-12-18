package org.foxesworld.animatix;

import org.foxesworld.animatix.animation.cache.CacheKey;
import org.foxesworld.animatix.animation.cache.ImageCache;
import org.foxesworld.animatix.animation.task.TaskExecutor;
import org.foxesworld.animatix.animation.effect.AnimationEffectFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.phase.AnimationPhaseExecutor;
import org.foxesworld.animatix.animation.config.AnimationConfigLoader;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.AnimationStatus;
import org.foxesworld.animatix.animation.config.AnimationConfig;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageWorks;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class AnimationFactory implements AnimationStatus {

    public static final System.Logger logger = System.getLogger(AnimationFactory.class.getName());
    private final TaskExecutor taskExecutor;
    private final AnimationConfigLoader configLoader = new AnimationConfigLoader();
    private final AnimationEffectFactory effectFactory;
    private final AnimationPhaseExecutor phaseExecutor;
    private final Map<CacheKey, List<AnimationFrame>> cachedFrames = new ConcurrentHashMap<>();
    private AnimationConfig config;
    private volatile boolean isPaused = false;
    private ImageWorks imageWorks;
    private final ImageCache imageCache;
    public AnimationFactory(String configPath) {
        this.taskExecutor = new TaskExecutor();
        this.effectFactory = new AnimationEffectFactory(this);
        this.phaseExecutor = new AnimationPhaseExecutor(this);
        this.imageCache = new ImageCache();
        loadConfig(configPath);
    }

    private void loadConfig(String configPath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Configuration file not found: " + configPath);
            }
            logger.log(System.Logger.Level.INFO, "Loading animation config...");
            this.config = configLoader.loadConfig(inputStream);
        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR, "Failed to initialize animation factory: " + e.getMessage(), e);
        }
    }

    public void createAnimation(Object window) {
        validateConfig();
        for (AnimationConfig.AnimConf animConf : config.getAnimObj()) {
            Rectangle objectBounds = animConf.getBounds();
            JLabel animLabel = new JLabel();
            animLabel.setName(animConf.getName());
            animLabel.setBounds(objectBounds);
            addLabelToWindow(window, animLabel);
            animLabel.setVisible(animConf.isVisible());
            taskExecutor.submitTask(() -> runAnimation(animLabel, animConf), System.out::println);
        }
    }

    private void addLabelToWindow(Object window, JLabel label) {
        if (window instanceof JFrame) {
            ((JFrame) window).add(label);
        } else if (window instanceof JWindow) {
            ((JWindow) window).add(label);
            ((JWindow) window).setLayout(null);
        } else {
            throw new IllegalArgumentException("Unsupported window type: " + window.getClass().getName());
        }
    }

    private void runAnimation(JLabel animLabel, AnimationConfig.AnimConf animConf) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            for (int phaseNum = 0; phaseNum < animConf.getPhases().size(); phaseNum++) {
                AnimationPhase phase = animConf.getPhases().get(phaseNum);
                waitIfPaused();
                phaseSetUp(animConf.getType(), animLabel, animConf, phase);
                delayBeforePhase(phase);
                List<AnimationFrame> frames = getOrCacheAnimationFrames(animConf, phase, animLabel);
                logger.log(System.Logger.Level.INFO, "Executing phase {0} of animation: {1}", phaseNum, animConf.getName());
                phaseExecutor.executePhase(frames, phase, phaseNum);
                waitForPhaseCompletion(phase.getDuration());
                logger.log(System.Logger.Level.INFO, "Phase {0} completed successfully.", phaseNum);
            }
            logger.log(System.Logger.Level.INFO, "Animation for {0} completed.", animConf.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(System.Logger.Level.ERROR, "Animation interrupted: {0}", e.getMessage(), e);
        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR, "Unexpected error during animation: {0}", e.getMessage(), e);
        } finally {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void waitIfPaused() throws InterruptedException {
        synchronized (this) {
            while (isPaused) {
                wait();
            }
        }
    }

    private void phaseSetUp(String type, JLabel label, AnimationConfig.AnimConf config, AnimationPhase phase) {
        switch (type) {
            case "text" -> setupTextPhase(config.getText(), label, phase);
            case "image" -> setupImagePhase(config.getImagePath(), label, phase);
            default -> throw new IllegalArgumentException("Unsupported animation type: " + type);
        }
    }

    private void setupTextPhase(String text, JLabel animLabel, AnimationPhase phase) {
        animLabel.setText(text);
        animLabel.setFont(new Font(phase.getFont(), Font.PLAIN, phase.getFontSize()));
        if (phase.getTextColor() != null) {
            animLabel.setForeground(Color.decode(phase.getTextColor()));
        }
        animLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupImagePhase(String imgPath, JLabel animLabel, AnimationPhase phase) {
        BufferedImage labelImage = ImageWorks.getImageFromStream(imgPath);
        if (phase.getAlpha() != 0) {
            labelImage = ImageWorks.setBaseAlpha(labelImage, (float) phase.getAlpha());
        }
        animLabel.setIcon(new ImageIcon(labelImage));
        this.imageWorks = new ImageWorks();
    }

    private List<AnimationFrame> getOrCacheAnimationFrames(AnimationConfig.AnimConf animConf, AnimationPhase phase, JLabel label) {
        CacheKey cacheKey = new CacheKey(animConf, phase);
        return cachedFrames.computeIfAbsent(cacheKey, key -> {
            List<AnimationFrame> frames = switch (animConf.getType()) {
                case "text" -> effectFactory.createTextEffects(phase, label);
                case "image" -> effectFactory.createImageEffects(phase, label);
                default -> Collections.emptyList();
            };
            return frames;
        });
    }

    private void delayBeforePhase(AnimationPhase phase) throws InterruptedException {
        if (phase.getDelay() > 0) {
            logger.log(System.Logger.Level.INFO, "Delaying phase for {0} ms", phase.getDelay());
            Thread.sleep(phase.getDelay());
        }
    }

    private void waitForPhaseCompletion(long duration) throws InterruptedException {
        Thread.sleep(duration);
    }

    public synchronized void pause() {
        isPaused = true;
        logger.log(System.Logger.Level.INFO, "Animation paused.");
    }

    public synchronized void resume() {
        isPaused = false;
        logger.log(System.Logger.Level.INFO, "Animation resumed.");
        notifyAll();
    }

    private void validateConfig() {
        if (config == null) {
            throw new IllegalStateException("AnimationConfig must be loaded before creating animation");
        }
    }

    public void dispose() {
        taskExecutor.shutdown();
    }

    @Override
    public synchronized void onPhaseCompleted(AnimationPhase phase) {
        logger.log(System.Logger.Level.INFO, "Phase {0} completed, notifying main thread.", phase.getPhaseNum());
        notify();
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public ImageWorks getImageWorks() {
        return imageWorks;
    }

    public ImageCache getImageCache() {
        return imageCache;
    }
}
