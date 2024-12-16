package org.foxesworld.animatix.animation.phase;

import org.foxesworld.animatix.AnimationFactory;
import org.foxesworld.animatix.animation.AnimationFrame;

import java.util.List;

public class AnimationPhaseExecutor {
    private AnimationFactory animationFactory;

    public AnimationPhaseExecutor() {
    }

    public void executePhase(AnimationFactory animationFactory, List<AnimationFrame> animationFrames, int phaseNum) {
        AnimationFactory.logger.log(System.Logger.Level.INFO, "Executing phase: {}", phaseNum);

        List<Runnable> tasks = animationFrames.stream()
                .<Runnable>map(animationFrame -> () -> {
                    try {
                        animationFrame.run();
                    } catch (Exception e) {
                        AnimationFactory.logger.log(System.Logger.Level.ERROR, "Error executing frame: {}", animationFrame.getClass().getSimpleName(), e);
                    }
                })
                .toList();

        try {
            animationFactory.getTaskExecutor().executeTasksWithTimeout(tasks, animationFactory.getCurrentPhase().getDuration());
            AnimationFactory.logger.log(System.Logger.Level.INFO, "Phase {} completed", phaseNum);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AnimationFactory.logger.log(System.Logger.Level.WARNING, "Phase execution interrupted: {}", phaseNum);
        }
        notifyPhaseCompleted();
    }

    private void notifyPhaseCompleted() {
        AnimationFactory.logger.log(System.Logger.Level.DEBUG, "Notifying factory about phase completion");
        animationFactory.onPhaseCompleted();
    }

    public void setAnimationFactory(AnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }
}
