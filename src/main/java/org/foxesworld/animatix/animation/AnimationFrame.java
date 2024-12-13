package org.foxesworld.animatix.animation;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.imageEffect.ImageWorks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public abstract class AnimationFrame implements Runnable {

    private final AnimationFactory animationFactory;
    private static final Logger logger = LoggerFactory.getLogger(AnimationFrame.class);
    protected AnimationPhase phase;
    protected ImageWorks imageWorks;
    protected JLabel label;
    private final long duration;
    private Timer timer;
    private long startTime;
    private long endTime;
    private boolean finished = false;

    public AnimationFrame(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
        this.phase = animationFactory.getCurrentPhase();
        this.imageWorks = animationFactory.getImageWorks();
        this.label = animationFactory.getAnimLabel();
        this.duration = phase.getDuration();
    }

    public abstract void update(float progress);


    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        endTime = startTime + duration;

        // Timer для обновлений UI
        timer = new Timer(1000 / 60, e -> updateFrame()); // 60 FPS
        timer.start();

        // После завершения анимации
        logger.info("Animation completed for phase: {}", animationFactory.getPhaseNum());
        animationFactory.incrementPhase();

        // Уведомление о завершении фазы
        ((AnimationStatus) animationFactory).onPhaseCompleted();
    }

    protected void updateFrame() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        float progress = Math.min((float) elapsedTime / duration, 1.0f);

        // Обновляем UI в EDT
        SwingUtilities.invokeLater(() -> update(progress));

        if (elapsedTime >= duration) {
            // Останавливаем таймер, когда анимация завершена
            timer.stop();
            finished = true;

            // Финальный апдейт
            SwingUtilities.invokeLater(() -> update(1.0f));
        }
    }
}
