package org.foxesworld.animatix;

import org.foxesworld.animatix.animation.area.KWindow;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AnimationFactory implements AnimationStatus {

    public static Logger logger;
    private final ScheduledExecutorService scheduler;
    private final AnimationConfigLoader configLoader;
    private final AnimationEffectFactory effectFactory;
    private final AnimationPhaseExecutor phaseExecutor;
    private int phaseNum = 0;
    private AnimationPhase currentPhase;
    private List<JLabel> animLabels = new ArrayList<>();
    private int labelIndex = 0;
    private ImageWorks imageWorks;
    private AnimationConfig config;
    private boolean isPaused = false;
    private final Map<AnimationPhase, List<AnimationFrame>> cachedFrames = new HashMap<>();

    public AnimationFactory(String configPath) {
        this(Executors.newSingleThreadScheduledExecutor(),
                new AnimationConfigLoader(),
                new AnimationEffectFactory(),
                new AnimationPhaseExecutor());
        System.setProperty("log.dir", System.getProperty("user.dir"));
        System.setProperty("log.level", "DEBUG");
        logger = LogManager.getLogger(Main.class);

        this.loadConfig(configPath);
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

    public AnimationFactory(ScheduledExecutorService scheduler,
                            AnimationConfigLoader configLoader,
                            AnimationEffectFactory effectFactory,
                            AnimationPhaseExecutor phaseExecutor) {
        this.scheduler = scheduler;
        this.configLoader = configLoader;
        this.effectFactory = effectFactory;
        this.phaseExecutor = phaseExecutor;

        this.effectFactory.setAnimationFactory(this);
        this.phaseExecutor.setAnimationFactory(this);
    }

    public void createAnimation(Object window) {
        validateConfig();
        for (AnimationConfig.ImageConfig imageConfig : config.getImages()) {
            JLabel animLabel = new JLabel(new ImageIcon(ImageWorks.getImageFromStream(imageConfig.getImagePath())));
            animLabel.setBounds(imageConfig.getBounds());
            addLabelToWindow(window, animLabel);
            this.animLabels.add(animLabel);
            this.imageWorks = new ImageWorks(animLabel, labelIndex);

            for (AnimationPhase phase : imageConfig.getPhases()) {
                new Thread(() -> executeAnimation(phase, imageConfig.getPhases().size(), imageConfig.isRepeat())).start();
                incrementPhase();
            }
            labelIndex += 1;
        }
    }

    private void addLabelToWindow(Object window, JLabel label) {
        if (window instanceof JFrame) {
            ((JFrame) window).add(label);
        } else if (window instanceof KWindow) {
            ((KWindow) window).add(label);
        } else if (window instanceof JWindow) {
            ((JWindow) window).add(label);
        } else {
            throw new IllegalArgumentException("Unsupported window type: " + window.getClass().getName());
        }
    }

    private int getWindowWidth(Object window) {
        if (window instanceof JFrame) {
            return ((JFrame) window).getWidth();
        } else if (window instanceof KWindow) {
            return ((KWindow) window).getWidth();
        } else if (window instanceof JWindow) {
            return ((JWindow) window).getWidth();
        } else {
            throw new IllegalArgumentException("Unsupported window type: " + window.getClass().getName());
        }
    }

    private void executeAnimation(AnimationPhase phase, int phasesAmount, boolean isRepeat) {
        try {
            do {
                currentPhase = phase;
                logger.info("Starting phase: {}", phase.getName());
                List<AnimationFrame> animationFrames = getOrCacheAnimationFrames(phase);
                phaseExecutor.executePhase(phase, animationFrames);
                waitForPhaseCompletion(phase.getDuration());
            } while (isRepeat && phaseNum < phasesAmount);
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
        scheduler.shutdown();
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
        if (imageWorks != null) {
            imageWorks.dispose();
            imageWorks = null;
        }
        scheduler.shutdown();
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
}
