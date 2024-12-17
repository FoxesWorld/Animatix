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
    private final Map<AnimationPhase, List<AnimationFrame>> cachedFrames = new ConcurrentHashMap<>();
    private AnimationConfig config;
    private volatile int phaseNum = 0;
    private volatile boolean isPaused = false;
    private boolean isImage = false;

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

        for (AnimationConfig.AnimConf animConf : config.getAnimObj()) {
            Rectangle objectBounds = animConf.getBounds();

            JLabel animLabel = new JLabel();
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
        try {
            do {
                for (AnimationPhase phase : animConf.getPhases()) {
                    if (isPaused) {
                        synchronized (this) {
                            wait();
                        }
                    }

                    if (animConf.getType().equals("text")) {
                        setupTextPhase(animConf.getText(), animLabel, phase);
                    } else if (animConf.getType().equals("image")) {
                        setupImagePhase(animConf.getImagePath(), animLabel, phase);
                    }

                    animLabel.setVisible(true);


                    if (phase.getDelay() > 0) {
                        logger.log(System.Logger.Level.INFO,
                                "Delaying phase {0} for {1} ms", phaseNum, phase.getDelay());
                        Thread.sleep(phase.getDelay());
                    }

                    List<AnimationFrame> frames = getOrCacheAnimationFrames(animConf, phase, animLabel);
                    logger.log(System.Logger.Level.INFO,
                            "Executing phase {0} of animation: {1}", phaseNum, animConf.getName());
                    phaseExecutor.executePhase(this, frames, phase, phaseNum);
                    waitForPhaseCompletion(phase.getDuration());
                    phaseNum++;
                }

            } while (animConf.isRepeat() && phaseNum < animConf.getPhases().size());

            logger.log(System.Logger.Level.INFO,
                    "Animation for {0} completed.", animConf.getName());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(System.Logger.Level.ERROR,
                    "Animation interrupted: {0}", e.getMessage(), e);

        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR,
                    "Unexpected error during animation: {0}", e.getMessage(), e);

        }
    }

    private void setupTextPhase(String text, JLabel animLabel, AnimationPhase phase) {
        animLabel.setText(text);
        animLabel.setFont(new Font(phase.getFont(), Font.PLAIN, phase.getFontSize()));
        if(phase.getTextColor() != null) {animLabel.setForeground(Color.decode(phase.getTextColor()));}
        animLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupImagePhase(String imgPath, JLabel animLabel, AnimationPhase phase) {
        // Получение изображения из пути
        BufferedImage labelImage = ImageWorks.getImageFromStream(imgPath);
        animLabel.setIcon(new ImageIcon(labelImage));

        if (phase.getAlpha() != 0) {
                labelImage = ImageWorks.setBaseAlpha(labelImage, (float) phase.getAlpha());
        }

        // Устанавливаем обновленное изображение
        animLabel.setIcon(new ImageIcon(labelImage));
    }


    private List<AnimationFrame> getOrCacheAnimationFrames(AnimationConfig.AnimConf animConf, AnimationPhase phase, JLabel label) {
        return cachedFrames.computeIfAbsent(phase, p -> {
            if (animConf.getType().equals("text")) {
                isImage = false;
                return effectFactory.createTextEffects(p, label);
            } else if (animConf.getType().equals("image")) {
                isImage = true;
                return effectFactory.createImageEffects(p, label);
            }
            return Collections.emptyList();
        });
    }

    private void waitForPhaseCompletion(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(System.Logger.Level.WARNING, "Phase wait interrupted. Duration: {0} ms", duration);
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
        logger.log(System.Logger.Level.INFO, "Phase {0} completed, notifying main thread.", phaseNum);
        notify();
    }

    public boolean isImage() {
        return isImage;
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public int getPhaseNum() {
        return phaseNum;
    }
}
