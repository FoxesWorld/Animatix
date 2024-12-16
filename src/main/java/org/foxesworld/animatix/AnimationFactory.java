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
    private final AnimationEffectFactory effectFactory = new AnimationEffectFactory();
    private final AnimationPhaseExecutor phaseExecutor = new AnimationPhaseExecutor();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<AnimationPhase, List<AnimationFrame>> cachedFrames = new ConcurrentHashMap<>();
    private final List<JLabel> animLabels = new ArrayList<>();
    private AnimationConfig config;
    private ImageWorks imageWorks;
    private volatile int phaseNum = 0;
    private volatile boolean isPaused = false;
    private AnimationPhase currentPhase;

    public AnimationFactory(String configPath) {
        this.taskExecutor = new TaskExecutor();
        effectFactory.setAnimationFactory(this);
        phaseExecutor.setAnimationFactory(this);
        loadConfig(configPath);
    }

    private void loadConfig(String configPath) {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(configPath)) {
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
        phaseNum = 0;

        for (AnimationConfig.ImageConfig imageConfig : config.getImages()) {
            Rectangle imageBounds = imageConfig.getBounds();
            BufferedImage labelImage = createSizedImage(ImageWorks.getImageFromStream(imageConfig.getImagePath()), imageBounds);

            JLabel animLabel = new JLabel(new ImageIcon(labelImage));
            animLabel.setBounds(imageBounds);
            addLabelToWindow(window, animLabel);
            animLabel.setVisible(false);
            animLabels.add(animLabel);

            imageWorks = new ImageWorks(animLabel, animLabels.size() - 1);

            executorService.submit(() -> runAnimation(imageConfig));
        }
    }

    private BufferedImage createSizedImage(BufferedImage image, Rectangle size) {
        BufferedImage resizedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose();

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

    private void runAnimation(AnimationConfig.ImageConfig imageConfig) {
        try {
            do {
                for (AnimationPhase phase : imageConfig.getPhases()) {
                    if (isPaused) synchronized (this) { wait(); }

                    currentPhase = phase;
                    JLabel animLabel = animLabels.get(imageWorks.getLabelIndex());
                    animLabel.setVisible(true);
                    animLabel.setIcon(new ImageIcon(imageWorks.setBaseAlpha((BufferedImage) ((ImageIcon)animLabel.getIcon()).getImage(), (float) phase.getAlpha())));
                    if (phase.getDelay() > 0) {
                        logger.log(System.Logger.Level.INFO, "Delaying phase {0} for {1} ms", phaseNum, phase.getDelay());
                        Thread.sleep(phase.getDelay());
                    }
                    List<AnimationFrame> frames = getOrCacheAnimationFrames(phase);

                    logger.log(System.Logger.Level.INFO, "Executing phase: " + phaseNum + " of " + imageConfig.getAnimationName());
                    phaseExecutor.executePhase(this, frames, phaseNum);
                    waitForPhaseCompletion(phase.getDuration());
                    phaseNum++;
                }
            } while (imageConfig.isRepeat() && phaseNum < imageConfig.getPhases().size());
            logger.log(System.Logger.Level.INFO, "Animation for " + imageConfig.getAnimationName() + " completed.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(System.Logger.Level.ERROR, "Animation interrupted: " + e.getMessage(), e);
        } finally {
            shutdownScheduler();
        }
    }

    private List<AnimationFrame> getOrCacheAnimationFrames(AnimationPhase phase) {
        return cachedFrames.computeIfAbsent(phase, effectFactory::createEffectsForPhase);
    }

    private void waitForPhaseCompletion(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AnimationFactory.logger.log(System.Logger.Level.WARNING, "Phase wait interrupted. Duration: {0} ms", duration);
            throw new RuntimeException("Phase wait interrupted", e);
        }
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

    private void shutdownScheduler() {
        executorService.shutdownNow();
    }

    private void validateConfig() {
        if (config == null) {
            throw new IllegalStateException("AnimationConfig must be loaded before creating animation");
        }
    }

    public void dispose() {
        animLabels.clear();
        if (imageWorks != null) {
            imageWorks.dispose();
        }
        shutdownScheduler();
    }

    @Override
    public synchronized void onPhaseCompleted(AnimationPhase phase) {
        logger.log(System.Logger.Level.INFO, "Phase {0} completed, notifying main thread.", phaseNum);
        notify();
    }

    public AnimationPhase getCurrentPhase() {
        return currentPhase;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public ImageWorks getImageWorks() {
        return imageWorks;
    }

    public int getPhaseNum() {
        return phaseNum;
    }

    public List<JLabel> getAnimLabels() {
        return animLabels;
    }
}
