package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationConfig;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.imageEffect.ImageWorks;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Phase {
    private final Map<AnimationPhase, java.util.List<AnimationFrame>> cachedFrames = new ConcurrentHashMap<>();
    private final AnimationFactory animationFactory;
    private final AnimationConfig.AnimConf animConf;
    private final JLabel animLabel;
    private AnimationPhase phase;
    private boolean isImage = false;
    public volatile boolean isPaused = false;
    public Phase(AnimationFactory animationFactory, AnimationConfig.AnimConf animConf, JLabel animLabel){
        this.animationFactory = animationFactory;
        this.animConf = animConf;
        System.out.println(animConf.getType());
        this.animLabel = animLabel;
    }

    public void preparePhase(AnimationPhase phase) {
        this.phase = phase;
        switch (animConf.getType()) {
            case "text" -> prepareTextPhase(animConf.getText(), animLabel, phase);
            case "image" -> prepareImagePhase(animConf.getImagePath(), animLabel, phase);
            default -> throw new IllegalArgumentException("Unsupported animation type: " + animConf.getType());
        }
        animLabel.setVisible(true);
    }

    private void prepareTextPhase(String text, JLabel animLabel, AnimationPhase phase) {
        animLabel.setText(text);
        animLabel.setFont(new Font(phase.getFont(), Font.PLAIN, phase.getFontSize()));
        if (phase.getTextColor() != null) {
            animLabel.setForeground(Color.decode(phase.getTextColor()));
        }
        animLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void prepareImagePhase(String imgPath, JLabel animLabel, AnimationPhase phase) {
        BufferedImage labelImage = ImageWorks.getImageFromStream(imgPath);

        if (labelImage == null) {
            AnimationFactory.logger.log(System.Logger.Level.ERROR, "Failed to load image: {0}", imgPath);
            return;
        }

        if (phase.getAlpha() > 0) {
            labelImage = ImageWorks.setBaseAlpha(labelImage, (float) phase.getAlpha());
        }

        BufferedImage finalImage = labelImage;
        SwingUtilities.invokeLater(() -> animLabel.setIcon(new ImageIcon(finalImage)));
    }


    public void executePhase(int phaseNum) {
        AnimationFactory.logger.log(System.Logger.Level.INFO,
                "Executing phase {0} of animation: {1}", phaseNum, animConf.getName());

        long delay = phase.getDelay();
        if (delay > 0) {
            executeWithDelay(() -> runPhaseFrames(phaseNum), delay);
        } else {
            runPhaseFrames(phaseNum);
        }

        waitForPhaseCompletion(phase.getDuration());
    }

    private void runPhaseFrames(int phaseNum) {
        List<AnimationFrame> frames = getOrCacheAnimationFrames();
        animationFactory.phaseExecutor.executePhase(animationFactory, frames, phase, phaseNum);
    }

    private void executeWithDelay(Runnable task, long delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                SwingUtilities.invokeLater(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                AnimationFactory.logger.log(System.Logger.Level.WARNING, "Delay interrupted: {0} ms", delay);
            }
        }).start();
    }



    private void waitForPhaseCompletion(long duration) {
        Timer timer = new Timer((int) duration, e -> {
            AnimationFactory.logger.log(System.Logger.Level.INFO, "Phase completed.");
        });
        timer.setRepeats(false);
        timer.start();
    }


    public synchronized List<AnimationFrame> getOrCacheAnimationFrames() {
        return cachedFrames.computeIfAbsent(phase, p -> {
            if (animConf.getType().equals("text")) {
                isImage = false;
                return animationFactory.getEffectFactory().createTextEffects(p, animLabel);
            } else if (animConf.getType().equals("image")) {
                isImage = true;
                return animationFactory.getEffectFactory().createImageEffects(p, animLabel);
            }
            return Collections.emptyList();
        });
    }


    public boolean isImage() {
        return isImage;
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
        synchronized (this) {
            notifyAll();
        }
    }

    public void waitIfPaused() throws InterruptedException {
        synchronized (this) {
            while (isPaused) {
                wait();
            }
        }
    }
}
