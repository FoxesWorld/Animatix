package org.foxesworld.animatix.animation.phase;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AnimationPhaseExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AnimationPhaseExecutor.class);
    private AnimationFactory animationFactory;

    public AnimationPhaseExecutor(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }

    public void executePhase(AnimationPhase phase, List<AnimationFrame> animationFrames) {
        logger.info("Executing phase: {}", phase.getName());
        CountDownLatch latch = new CountDownLatch(animationFrames.size());

        for (AnimationFrame animationFrame : animationFrames) {
            new Thread(() -> {
                try {
                    animationFrame.run();
                    logger.debug("Frame completed: {}", animationFrame.getClass().getSimpleName());
                } catch (Exception e) {
                    logger.error("Error executing frame: {}", animationFrame.getClass().getSimpleName(), e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        try {
            latch.await();
            logger.info("Phase {} completed", phase.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Phase execution interrupted: {}", phase.getName());
        }

        notifyPhaseCompleted();
    }

    private void notifyPhaseCompleted() {
        logger.debug("Notifying factory about phase completion");
        animationFactory.onPhaseCompleted();
    }

    public void setAnimationFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }
}
