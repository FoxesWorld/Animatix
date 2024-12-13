package org.example.animation;

import org.example.animation.config.AnimationConfigLoader;
import org.example.animation.config.AnimationPhase;
import org.example.animation.imageEffect.AnimationFrame;
import org.example.animation.imageEffect.AnimationStatus;
import org.example.animation.config.AnimationConfig;
import org.example.animation.imageEffect.ImageWorks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AnimationFactory implements AnimationStatus {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static final Logger logger = LoggerFactory.getLogger(AnimationFactory.class);
    private int phaseNum = 0;
    private AnimationPhase currentPhase;
    private JLabel animLabel;
    private ImageWorks imageWorks;
    private AnimationConfig config;
    private final AnimationEffectFactory effectFactory;
    private final AnimationPhaseExecutor phaseExecutor;
    private final AnimationConfigLoader configLoader;

    public AnimationFactory() {
        configLoader = new AnimationConfigLoader();
        phaseExecutor = new AnimationPhaseExecutor(this);
        effectFactory = new AnimationEffectFactory(this);
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
        if (config == null) {
            throw new IllegalStateException("AnimationConfig must be loaded before creating animation");
        }

        this.imageWorks = new ImageWorks(ImageWorks.getImageFromStream(config.getImagePath()));
        animLabel = new JLabel(new ImageIcon(imageWorks.getImage()));
        animLabel.setBounds(50, 50, 100, 100);
        frame.add(animLabel);

        List<AnimationPhase> phases = config.getPhases();
        boolean repeat = config.isRepeat();

        new Thread(() -> {
            do {
                for (AnimationPhase phase : phases) {
                    this.currentPhase = phase;
                    logger.info("Starting phase: {}", phase.getTypes());

                    // Создаем список всех эффектов для текущей фазы
                    List<AnimationFrame> animationFrames = effectFactory.createEffectsForPhase(phase);

                    // Запускаем фазу
                    phaseExecutor.executePhase(phase, animationFrames);

                    // Ожидаем завершения фазы
                    synchronized (this) {
                        try {
                            // Планируем таймер для пробуждения текущего потока
                            scheduler.schedule(() -> {
                                synchronized (this) {
                                    logger.info("Timer expired, notifying...");
                                    this.notify(); // Уведомляем поток
                                }
                            }, currentPhase.getDuration(), TimeUnit.MILLISECONDS);

                            logger.info("Waiting for phase completion...");
                            this.wait(); // Ждём уведомления
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            logger.error("Phase waiting interrupted", e);
                        }
                    }
                }

                incrementPhase();
            } while (repeat && phaseNum < config.getPhases().size());

            logger.info("Animation complete.");
            System.exit(0);
        }).start();
    }

    @Override
    public synchronized void onPhaseCompleted() {
        logger.info("Phase completed, notifying main thread.");
        notify();
    }

    public synchronized void incrementPhase() {
        this.phaseNum++;
        logger.debug("Phase incremented to: {}", phaseNum);
    }

    public JLabel getAnimLabel() {
        return animLabel;
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
