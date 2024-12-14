package org.foxesworld.animatix;

import org.foxesworld.animatix.animation.effect.AnimationEffectFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.phase.AnimationPhaseExecutor;
import org.foxesworld.animatix.animation.config.AnimationConfigLoader;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.AnimationStatus;
import org.foxesworld.animatix.animation.config.AnimationConfig;
import org.foxesworld.animatix.animation.imageEffect.ImageWorks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AnimationFactory implements AnimationStatus {

    private static final Logger logger = LoggerFactory.getLogger(AnimationFactory.class);

    private final ScheduledExecutorService scheduler;
    private final AnimationConfigLoader configLoader;
    private final AnimationEffectFactory effectFactory;
    private final AnimationPhaseExecutor phaseExecutor;

    private int phaseNum = 0;
    private AnimationPhase currentPhase;
    private JLabel animLabel, textLabel;
    private ImageWorks imageWorks;
    private AnimationConfig config;

    public AnimationFactory() {
        this(Executors.newSingleThreadScheduledExecutor(),
                new AnimationConfigLoader(),
                new AnimationEffectFactory(null),
                new AnimationPhaseExecutor(null));
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

    public void loadConfig(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        logger.info("Loading animation config...");
        this.config = configLoader.loadConfig(inputStream);
        logger.info("Animation config loaded successfully: {}", config);
    }

    public void createAnimation(JFrame frame) {
        validateConfig();

        this.imageWorks = new ImageWorks(ImageWorks.getImageFromStream(config.getImagePath()));
        animLabel = new JLabel(new ImageIcon(imageWorks.getImage()));
        animLabel.setBounds(this.config.getBounds());
        frame.add(animLabel);

        textLabel = new JLabel("", SwingConstants.CENTER);
        textLabel.setBounds(0, 200, frame.getWidth(), 50);
        frame.add(textLabel);

        List<AnimationPhase> phases = config.getPhases();
        boolean repeat = config.isRepeat();

        new Thread(() -> executeAnimation(phases, repeat)).start();
    }

    private void executeAnimation(List<AnimationPhase> phases, boolean repeat) {
        do {
            for (AnimationPhase phase : phases) {
                currentPhase = phase;
                logger.info("Starting phase: {}", phase.getName());

                List<AnimationFrame> animationFrames = effectFactory.createEffectsForPhase(phase);
                phaseExecutor.executePhase(phase, animationFrames);

                waitForPhaseCompletion(phase.getDuration());
            }
            incrementPhase();
        } while (repeat && phaseNum < config.getPhases().size());

        logger.info("Animation complete.");
        shutdownScheduler();
    }

    private void waitForPhaseCompletion(long duration) {
        synchronized (this) {
            try {
                scheduler.schedule(() -> {
                    synchronized (this) {
                        logger.info("Timer expired, notifying...");
                        notify();
                    }
                }, duration, TimeUnit.MILLISECONDS);

                logger.info("Waiting for phase completion...");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Phase waiting interrupted", e);
            }
        }
    }

    public void shutdownScheduler() {
        scheduler.shutdown();
    }

    @Override
    public synchronized void onPhaseCompleted() {
        logger.info("Phase completed, notifying main thread.");
        notify();
    }

    public synchronized void incrementPhase() {
        phaseNum++;
        logger.debug("Phase incremented to: {}", phaseNum);
    }

    private void validateConfig() {
        if (config == null) {
            throw new IllegalStateException("AnimationConfig must be loaded before creating animation");
        }
    }

    public JLabel getAnimLabel() {
        return animLabel;
    }

    public JLabel getTextLabel() {
        return textLabel;
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
