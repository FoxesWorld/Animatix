package org.foxesworld.animatix;

import org.foxesworld.animatix.animation.cache.CacheKey;
import org.foxesworld.animatix.animation.cache.ImageCache;
import org.foxesworld.animatix.animation.config.Animation;
import org.foxesworld.animatix.animation.config.Phase;
import org.foxesworld.animatix.animation.element.BaseAnimationElement;
import org.foxesworld.animatix.animation.element.ImageAnimationElement;
import org.foxesworld.animatix.animation.element.TextAnimationElement;
import org.foxesworld.animatix.animation.task.TaskExecutor;
import org.foxesworld.animatix.animation.effect.AnimationEffectFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.phase.AnimationPhaseExecutor;
import org.foxesworld.animatix.animation.config.AnimationConfigLoader;
import org.foxesworld.animatix.animation.AnimationStatus;
import org.foxesworld.animatix.animation.config.AnimationConfig;
import org.foxesworld.animatix.animation.effect.imageEffect.ImageWorks;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class AnimationFactory implements AnimationStatus {

    public static final System.Logger logger = System.getLogger(AnimationFactory.class.getName());
    private final TaskExecutor taskExecutor;
    private final AnimationEffectFactory effectFactory;
    private final AnimationPhaseExecutor phaseExecutor;
    private final Map<CacheKey, List<AnimationFrame>> cachedFrames = new ConcurrentHashMap<>();
    private final AnimationConfig config;
    private final ImageWorks imageWorks;
    private final ImageCache imageCache;
    private volatile boolean isPaused = false;
    private volatile boolean isRunning = false;
    private volatile boolean isStopped = true;

    public AnimationFactory(String configPath) {
        this.taskExecutor = new TaskExecutor();
        this.effectFactory = new AnimationEffectFactory(this);
        this.phaseExecutor = new AnimationPhaseExecutor(this);
        this.imageCache = new ImageCache();
        this.imageWorks = new ImageWorks();
        AnimationConfigLoader configLoader = new AnimationConfigLoader();
        this.config = configLoader.loadConfig(configPath);
    }


    public void createAnimation(Object window) {
        validateConfig();
        isStopped = false;
        isRunning = true;
        for (Animation animation : config.getAnimObj()) {
            BaseAnimationElement animationElement = switch (animation.getType()) {
                case "text" -> new TextAnimationElement(
                        animation.getName(),
                        animation.getBounds(),
                        animation.isVisible(),
                        animation.getText(),
                        new Font("Default", Font.PLAIN, 12),
                        Color.BLACK
                );
                case "image" -> new ImageAnimationElement(
                        animation.getName(),
                        animation.getBounds(),
                        animation.isVisible(),
                        ImageWorks.getImageFromStream(animation.getImagePath())
                );
                default -> throw new IllegalArgumentException("Unsupported animation type: " + animation.getType());
            };

            JComponent component = createAnimationComponent(animationElement);
            addLabelToWindow(window, component);
            taskExecutor.submitTask(() -> runAnimation((JLabel) component, animation), System.out::println);
        }
    }

    private JComponent createAnimationComponent(BaseAnimationElement element) {
        return element.createComponent();
    }

    private void addLabelToWindow(Object window, JComponent component) {
        if (window instanceof JFrame) {
            ((JFrame) window).add(component);
        } else if (window instanceof JWindow) {
            ((JWindow) window).add(component);
            ((JWindow) window).setLayout(null);
        } else if (window instanceof JPanel) {
            ((JPanel) window).add(component);
            ((JPanel) window).setLayout(null);
        } else if (window instanceof JDialog) {
            ((JDialog) window).add(component);
            ((JDialog) window).setLayout(null);
        } else {
            throw new IllegalArgumentException("Unsupported window type: " + window.getClass().getName());
        }
    }

    private void runAnimation(JLabel animLabel, Animation animation) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            for (Phase phase : animation.getPhases()) {
                waitIfPaused();
                setupPhase(animation.getType(), animLabel, animation, phase);
                delayBeforePhase(phase);
                List<AnimationFrame> frames = getOrCacheAnimationFrames(animation, phase, animLabel);
                phaseExecutor.executePhase(frames, phase, animation.getPhases().indexOf(phase));
                waitForPhaseCompletion(phase.getDuration());
                phase.setPhaseNum(animation.getPhases().size());
            }
            logger.log(System.Logger.Level.INFO, "Animation for {0} completed.", animation.getName());
            isRunning = false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(System.Logger.Level.ERROR, "Animation interrupted: {0}", e.getMessage(), e);
        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR, "Unexpected error during animation: {0}", e.getMessage(), e);
        } finally {
            shutdownScheduler(scheduler);
        }
    }

    private void waitIfPaused() throws InterruptedException {
        synchronized (this) {
            while (isPaused) {
                wait();
            }
        }
    }

    private void setupPhase(String type, JLabel label, Animation config, Phase phase) {
        switch (type) {
            case "text" -> setupTextPhase(config.getText(), label, phase);
            case "image" -> setupImagePhase(config.getImagePath(), label, phase);
            default -> throw new IllegalArgumentException("Unsupported animation type: " + type);
        }
    }

    private void setupTextPhase(String text, JLabel animLabel, Phase phase) {
        animLabel.setText(text);
        animLabel.setFont(new Font(phase.getFont(), Font.PLAIN, phase.getFontSize()));
        if (phase.getTextColor() != null) {
            animLabel.setForeground(Color.decode(phase.getTextColor()));
        }
        animLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupImagePhase(String imgPath, JLabel animLabel, Phase phase) {
        BufferedImage labelImage = ImageWorks.getImageFromStream(imgPath);
        if (phase.getAlpha() != 0) {
            labelImage = ImageWorks.setBaseAlpha(labelImage, (float) phase.getAlpha());
        }
        animLabel.setIcon(new ImageIcon(labelImage));

    }

    private List<AnimationFrame> getOrCacheAnimationFrames(Animation animation, Phase phase, JLabel label) {
        CacheKey cacheKey = new CacheKey(animation, phase);
        return cachedFrames.computeIfAbsent(cacheKey, key -> switch (animation.getType()) {
            case "text" -> effectFactory.createTextEffects(phase, label);
            case "image" -> effectFactory.createImageEffects(phase, label);
            default -> Collections.emptyList();
        });
    }

    private void delayBeforePhase(Phase phase) throws InterruptedException {
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
        isRunning = false;
        logger.log(System.Logger.Level.INFO, "Animation paused.");
    }

    public synchronized void resume() {
        isPaused = false;
        isRunning = true;
        logger.log(System.Logger.Level.INFO, "Animation resumed.");
        notifyAll();
    }

    public synchronized void stop() {
        isPaused = false;
        isRunning = false;
        isStopped = true;
        taskExecutor.shutdown();
        logger.log(System.Logger.Level.INFO, "Animation stopped.");
    }

    private void validateConfig() {
        if (config == null) {
            throw new IllegalStateException("AnimationConfig must be loaded before creating animation");
        }
    }

    private void shutdownScheduler(ScheduledExecutorService scheduler) {
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

    public void dispose() {
        taskExecutor.shutdown();
    }

    @Override
    public synchronized void onPhaseCompleted(Phase phase) {
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

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isStopped() {
        return isStopped;
    }
}
