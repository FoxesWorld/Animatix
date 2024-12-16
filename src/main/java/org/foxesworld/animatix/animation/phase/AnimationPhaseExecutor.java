package org.foxesworld.animatix.animation.phase;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AnimationPhaseExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AnimationPhaseExecutor.class);
    private AnimationFactory animationFactory;

    public AnimationPhaseExecutor() {
    }

    public void executePhase(AnimationFactory animationFactory, List<AnimationFrame> animationFrames, int phaseNum) {
        logger.info("Executing phase: {}", phaseNum);

        List<Runnable> tasks = animationFrames.stream()
                .<Runnable>map(animationFrame -> () -> {
                    try {
                        animationFrame.run();
                    } catch (Exception e) {
                        logger.error("Error executing frame: {}", animationFrame.getClass().getSimpleName(), e);
                    }
                })
                .toList();

        try {
            animationFactory.getTaskExecutor().executeTasksWithTimeout(tasks, animationFactory.getCurrentPhase().getDuration());
            logger.info("Phase {} completed", phaseNum);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Phase execution interrupted: {}", phaseNum);
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
